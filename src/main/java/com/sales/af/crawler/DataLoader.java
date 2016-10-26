package com.sales.af.crawler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.sales.af.bo.Brand;
import com.sales.af.bo.Category;
import com.sales.af.bo.Gender;
import com.sales.af.bo.Image;
import com.sales.af.bo.Product;
import com.sales.af.bo.SnapshotDetail;
import com.sales.af.dao.BrandDao;
import com.sales.af.dao.CategoryDao;
import com.sales.af.dao.GenderDao;
import com.sales.af.dao.ProductDao;
import com.sales.af.to.SnapshotDetailTo;

public class DataLoader {
	private static Logger LOGGER = Logger.getLogger(DataLoader.class);
	
	private static ConcurrentHashMap<String, Long> existingBrand;
	private static ConcurrentHashMap<String, Long> existingGender;
	private static ConcurrentHashMap<String, Long> existingCategory;
	private static Map<Long, Map<String, Product>> existingProducts;
	
	@Autowired
	ProductQueueService productQueueService;
	
	@Autowired
	GenderDao genderDao;

	@Autowired
	BrandDao brandDao;

	@Autowired
	CategoryDao categoryDao;

	@Autowired
	ProductDao productDao;
	
	public DataLoader() {
		LOGGER.info("Initializing...");
		
		existingBrand = new ConcurrentHashMap<String, Long>();
		existingGender = new ConcurrentHashMap<String, Long>();
		existingCategory = new ConcurrentHashMap<String, Long>();
		existingProducts = new HashMap<Long, Map<String, Product>>();
		
		LOGGER.info("Done initializing!");
	}
	
	@Transactional
	public void load() {
		Set<SnapshotDetailTo> products = productQueueService.pollProductQueue();
		Map<Long, SnapshotDetailTo> snapshotTo = new HashMap<Long, SnapshotDetailTo>();
		
		if(products == null) {
			return;
		}
		
		if(!products.isEmpty()) {
			for(SnapshotDetailTo product : products) {
				String brandName = product.getBrandName();
				Long brandId = getBrandId(brandName);
				
				String genderName = product.getGenderName();
				Long genderId = getGenderId(genderName);
				
				String categoryName = product.getCategoryName();
				Long categoryId = getCategoryId(categoryName);
				
				String dataProductId = product.getProductDataId();
				
				if(!existingProducts.containsKey(brandId)) {
					Map<String, Product> p = productDao.getProductByBrandId(brandId);
					if(p != null) {
						existingProducts.put(brandId, p);
					} 
				}
				
				if(!existingProducts.get(brandId).containsKey(dataProductId)) {
					Product p = new Product();
					
					Brand brand = new Brand();
					brand.setId(brandId);
					p.setBrand(brand);
					
					Gender gender = new Gender();
					gender.setId(genderId);
					p.setGender(gender);
					
					Category category = new Category();
					category.setId(categoryId);
					p.setCategory(category);
					
					p.setCount(1);
					
					List<Image> images = new ArrayList<Image>();
					List<SnapshotDetail> snapshots = new ArrayList<SnapshotDetail>();
					
					for(String imageUrl : product.getImages()) {
						Image image = new Image();
						image.setImageUrl(imageUrl);
						images.add(image);
					}
					
					p.setName(product.getProductName());
					p.setImages(images);
					p.setSnapshotDetail(snapshots);
					p.setProductId(dataProductId);
					p.setProductUrl(product.getProductUrl());
					
					p = productDao.insertProduct(p);
					
					existingProducts.get(brandId).put(p.getProductId(), p);
					product.setProductId(p.getId());
					
					LOGGER.info(String.format(
							"Insert Product %s ID %s into DB; Category: %s; Gender: %s; Product ID: %s; List Price: %s; Offer Price: %s",
							p.getId(), p.getName(), product.getCategoryName(), product.getGenderName(), p.getProductId(),
							product.getPriceRegular(),
							product.getPriceDiscount()));
				} 
				else {
					product.setProductId(existingProducts.get(brandId).get(dataProductId).getId());
					
					if(!product.getProductUrl().equals(existingProducts.get(brandId).get(dataProductId).getProductUrl())) {
						existingProducts.get(brandId).get(dataProductId).setProductUrl(product.getProductUrl());
						existingProducts.get(brandId).get(dataProductId).setUpdateDate(new Date());
						
						productDao.updateProductUrl(product.getProductId(), product.getProductUrl());
					}
				}
				
				snapshotTo.put(product.getProductId(), product);
			}
		}
		else {
			LOGGER.warn("Produc from Product Queue is empty!");
		}
		
		if(!snapshotTo.isEmpty()) {
			productQueueService.addSnapshot(snapshotTo);
		}
		
		LOGGER.info(String.format("Done running data loader! Number of products to load: %s.", snapshotTo.size()));
	}
	
	private Long getBrandId(String brandName) {
		if(!existingBrand.containsKey(brandName)) {
			Brand brand = brandDao.getBrandByName(brandName);
			
			if(brand == null) {
				LOGGER.info(String.format("Inserting brand %s into DB", brandName));
				brand = brandDao.insertBrand(brandName);
			} 
			else {
				LOGGER.info(String.format("Brand %s already exists in the DB", brandName));
			}
			
			existingBrand.put(brandName, brand.getId());
		}
		
		return existingBrand.get(brandName);
	}
	
	private Long getGenderId(String genderName) {
		if(!existingGender.containsKey(genderName)) {
			Gender gender = genderDao.getGenderByName(genderName);
			
			if(gender == null) {
				LOGGER.info(String.format("Inserting gender %s into DB", genderName));
				gender = genderDao.insertGender(genderName);
			} 
			else {
				LOGGER.info(String.format("Gender %s already exists in the DB", genderName));
			}
			
			existingGender.put(genderName, gender.getId());
		}
		
		return existingGender.get(genderName);
	}
	
	private Long getCategoryId(String categoryName) {
		if(!existingCategory.containsKey(categoryName)) {
			Category category = categoryDao.getCategoryByName(categoryName);
			
			if(category == null) {
				LOGGER.info(String.format("Insert category %s into DB", categoryName));
				category = categoryDao.insertCategory(categoryName);
			} 
			else {
				LOGGER.info(String.format("Category %s already exists in the DB", categoryName.toLowerCase()));
			}
			
			existingCategory.put(categoryName, category.getId());
		}
		
		return existingCategory.get(categoryName);
	}
}
