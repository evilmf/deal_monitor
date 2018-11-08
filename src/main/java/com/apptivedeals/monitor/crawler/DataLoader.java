package com.apptivedeals.monitor.crawler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.apptivedeals.monitor.dao.BrandDao;
import com.apptivedeals.monitor.dao.CategoryDao;
import com.apptivedeals.monitor.dao.GenderDao;
import com.apptivedeals.monitor.dao.ImageDao;
import com.apptivedeals.monitor.dao.ProductDao;
import com.apptivedeals.monitor.to.Image;
import com.apptivedeals.monitor.to.Product;
import com.apptivedeals.monitor.to.Snapshot.SnapshotDetail;

@Component
public class DataLoader {
	private static Logger LOGGER = Logger.getLogger(DataLoader.class);
	
	private static ConcurrentHashMap<String, Long> existingBrand;
	private static ConcurrentHashMap<String, Long> existingGender;
	private static ConcurrentHashMap<String, Long> existingCategory;
	private static Map<Long, Map<String, Product>> existingProducts;
	
	@Autowired
	private ProductQueueService productQueueService;
	
	@Autowired
	private GenderDao genderDao;

	@Autowired
	private BrandDao brandDao;

	@Autowired
	private CategoryDao categoryDao;

	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private ImageDao imageDao;
	
	public DataLoader() {
		LOGGER.info("Initializing...");
		
		existingBrand = new ConcurrentHashMap<String, Long>();
		existingGender = new ConcurrentHashMap<String, Long>();
		existingCategory = new ConcurrentHashMap<String, Long>();
		existingProducts = new HashMap<Long, Map<String, Product>>();
		
		LOGGER.info("Done initializing!");
	}
	
	@Scheduled(fixedDelay = 5000)
	public void load() {
		Set<SnapshotDetail> products = productQueueService.pollProductQueue();
		Map<Long, SnapshotDetail> snapshotTo = new HashMap<Long, SnapshotDetail>();
		
		if(products == null) {
			return;
		}
		
		LOGGER.info("Running data loader...");
		
		if(!products.isEmpty()) {
			for(SnapshotDetail product : products) {
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
					p.setProductId(product.getProductDataId());
					p.setBrandId(brandId);
					p.setGenderId(genderId);
					p.setCategoryId(categoryId);
					p.setName(product.getProductName());
					p.setUrl(product.getProductUrl());
					p.setCreateDate(new Date());
					p.setUpdateDate(new Date());
					p.setActive(true);
					
					long pid = productDao.insertProduct(p);
					p.setId(pid);
					product.setProductId(pid);
					
					for(String imageUrl : product.getImages()) {
						Image image = new Image();
						image.setUrl(imageUrl);
						image.setProductId(pid);
						image.setCreateDate(new Date());
						image.setUpdateDate(new Date());
						image.setActive(true);
						
						imageDao.insertImage(image);
					} 
					
					existingProducts.get(brandId).put(p.getProductId(), p);
					
					LOGGER.info(String.format(
							"Insert Product %s ID %s into DB; Category: %s; Gender: %s; Product ID: %s; List Price: %s; Offer Price: %s",
							p.getId(), p.getName(), product.getCategoryName(), product.getGenderName(), p.getProductId(),
							product.getPriceRegular(),
							product.getPriceDiscount()));
				} 
				else {
					product.setProductId(existingProducts.get(brandId).get(dataProductId).getId());
					
					if(!product.getProductUrl().equals(existingProducts.get(brandId).get(dataProductId).getUrl())) {
						existingProducts.get(brandId).get(dataProductId).setUrl(product.getProductUrl());
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
			Long brandId = brandDao.getBrandId(brandName);
			
			if(brandId == null) {
				LOGGER.info(String.format("Inserting brand %s into DB", brandName));
				brandId = brandDao.insertBrand(brandName);
			} 
			else {
				LOGGER.info(String.format("Brand %s already exists in the DB", brandName));
			}
			
			existingBrand.put(brandName, brandId);
		}
		
		return existingBrand.get(brandName);
	}
	
	private Long getGenderId(String genderName) {
		if(!existingGender.containsKey(genderName)) {
			Long genderId = genderDao.getGenderId(genderName);
			
			if(genderId == null) {
				LOGGER.info(String.format("Inserting gender %s into DB", genderName));
				genderId = genderDao.insertGender(genderName);
			} 
			else {
				LOGGER.info(String.format("Gender %s already exists in the DB", genderName));
			}
			
			existingGender.put(genderName, genderId);
		}
		
		return existingGender.get(genderName);
	}
	
	private Long getCategoryId(String categoryName) {
		if(!existingCategory.containsKey(categoryName)) {
			Long categoryId = categoryDao.getCategoryId(categoryName);
			
			if(categoryId == null) {
				LOGGER.info(String.format("Insert category %s into DB", categoryName));
				categoryId = categoryDao.insertCategory(categoryName);
			} 
			else {
				LOGGER.info(String.format("Category %s already exists in the DB", categoryName.toLowerCase()));
			}
			
			existingCategory.put(categoryName, categoryId);
		}
		
		return existingCategory.get(categoryName);
	}
}
