package com.sales.af.crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.sales.af.to.ProductsTo;
import com.sales.af.to.SnapshotDetailTo;
import com.sales.af.to.SnapshotTo;

public class DataLoader extends ProductQueue {
	private static Logger logger = Logger.getLogger(DataLoader.class);
	private SnapshotTo allProducts;
	private String brandName;
	private Set<String> genderNames;
	private Set<String> categoryNames;
	private Set<String> productDataIds;

	@Autowired
	GenderDao genderDao;

	@Autowired
	BrandDao brandDao;

	@Autowired
	CategoryDao categoryDao;

	@Autowired
	ProductDao productDao;

	public void load() throws InterruptedException {
		allProducts = productQueue.poll();
		if (allProducts != null && allProducts.getSnapshotDetail().size() > 0) {
			long startTime = System.currentTimeMillis();
			setBrandInfo(allProducts);
			logger.info(String.format("Start Loading Products for Brand: %s; Queue Size: %s",
					allProducts.getBrandId(), productQueue.size()));

			loadBrand();
			loadGender();
			loadCategory();
			loadProduct();

			addToSnapshotQueue();

			long endTime = System.currentTimeMillis();
			logger.info(String.format("Done Loading for Brand %s; Queue Size: %s; Duration: %s ms",
					allProducts.getBrandId(), productQueue.size(), endTime - startTime));
		}
	}

	private void loadBrand() {
		if (existingBrand.containsKey(brandName)) {
			logger.debug(String.format("Brand %s exists in memory.", brandName));
			allProducts.setBrandId(existingBrand.get(brandName)); 

		} else {
			logger.info(String.format("Loading brand %s into DB and memory.", brandName));
			Brand brand = brandDao.getBrandByName(brandName);

			if (brand == null) {
				logger.info(String.format("Inserting brand %s into DB", brandName));
				brand = brandDao.insertBrand(brandName);
			} else {
				logger.info(String.format("Brand %s already exists in the DB", brandName));
			}
			
			allProducts.setBrandId(brand.getId());
			existingBrand.put(brandName, allProducts.getBrandId());
			logger.debug(String.format("Brand %s has id %s", brandName, allProducts.getBrandId()));
		}
	}

	private void loadGender() {
		for (String genderName : genderNames) {
			if (existingGender.containsKey(genderName.toLowerCase())) {
				logger.debug(String.format("Gender %s exists in memory.", genderName));
				//allProducts.getGenders().get(genderName)
				//		.setId(existingGender.get(genderName.toLowerCase()).longValue());
			} else {
				logger.info(String.format("Loading gender %s into DB and memory", genderName));
				Gender gender = genderDao.getGenderByName(genderName.toLowerCase());

				if (gender == null) {
					logger.debug(String.format("Insert gender %s into DB", genderName));
					//allProducts.getGenders().put(genderName,
					//		genderDao.insertGender(allProducts.getGenders().get(genderName)));
					gender = genderDao.insertGender(genderName);

				} else {
					logger.debug(String.format("Gender %s already exists in the DB", genderName.toLowerCase()));
					//allProducts.getGenders().put(genderName, gender);
				}

				existingGender.put(genderName, gender.getId());
				logger.debug(String.format("Gender %s has id %s", genderName,
						gender.getId()));
			}

		}
	}

	private void loadCategory() {
		for (String categoryName : categoryNames) {
			if (existingCategory.containsKey(categoryName.toLowerCase())) {
				logger.debug(String.format("Category %s exists in memory.", categoryName));
				//allProducts.getCategories().get(categoryName)
				//		.setId(existingCategory.get(categoryName.toLowerCase()).longValue());
			} else {
				logger.debug(String.format("Loading category %s into DB and memory", categoryName));
				Category category = categoryDao.getCategoryByName(categoryName.toLowerCase());

				if (category == null) {
					logger.debug(String.format("Insert category %s into DB", categoryName));
					//allProducts.getCategories().put(categoryName,
					//		categoryDao.insertCategory(allProducts.getCategories().get(categoryName)));
					category = categoryDao.insertCategory(categoryName);
				} else {
					logger.debug(String.format("Category %s already exists in the DB", categoryName.toLowerCase()));
					//allProducts.getCategories().put(categoryName, category);
				}

				existingCategory.put(categoryName, category.getId());
				logger.debug(String.format("Category %s has id %s", categoryName,
						category.getId()));
			}
		}
	}

	private void loadProduct() {
		long startTime = System.currentTimeMillis();
		Map<String, Product> existingProducts = productDao.getProductByProdIdAndBrandId(
				new ArrayList<String>(productDataIds), allProducts.getBrandId());
		for (SnapshotDetailTo snapshotDetailTo : allProducts.getSnapshotDetail().values()) {
			Product product = new Product();
			String dataProductId = snapshotDetailTo.getProductDataId();
			if (!existingProducts.containsKey(dataProductId)) {
				Brand brand = new Brand();
				product.setBrand(brand);
				Gender gender = new Gender();
				product.setGender(gender);
				Category category = new Category();
				product.setCategory(category);
				
				product.getBrand().setId(allProducts.getBrandId());
				product.getGender().setId(existingGender.get(snapshotDetailTo.getGenderName().toLowerCase()));
				product.getCategory().setId(existingCategory.get(snapshotDetailTo.getCategoryName().toLowerCase()));
				product.setCount(1);
				
				List<Image> images = new ArrayList<Image>();
				List<SnapshotDetail> snapshots = new ArrayList<SnapshotDetail>();
				
				for(String imageUrl : snapshotDetailTo.getImages()) {
					Image image = new Image();
					image.setImageUrl(imageUrl);
					images.add(image);
				}
				
				product.setName(snapshotDetailTo.getProductName());
				product.setImages(images);
				product.setSnapshotDetail(snapshots);
				product.setProductId(dataProductId);
				product.setProductUrl(snapshotDetailTo.getProductUrl());

				Product p = productDao.insertProduct(product);
//				p.getSnapshotDetail().get(0).setPriceDiscount(
//						allProducts.getProducts().get(dataProductId).getSnapshotDetail().get(0).getPriceDiscount());
//				p.getSnapshotDetail().get(0).setPriceRegular(
//						allProducts.getProducts().get(dataProductId).getSnapshotDetail().get(0).getPriceRegular());
//				allProducts.getProducts().put(dataProductId, p);
				snapshotDetailTo.setProductId(p.getId());
				logger.debug(String.format(
						"Insert Product %s ID %s into DB; Category: %s; Gender: %s; Product ID: %s; List Price: %s; Offer Price: %s",
						p.getId(), p.getName(), snapshotDetailTo.getCategoryName(), snapshotDetailTo.getGenderName(), p.getProductId(),
						snapshotDetailTo.getPriceRegular(),
						snapshotDetailTo.getPriceDiscount()));
			} else {
				logger.debug(String.format(
						"Product %s already exists in DB; Category: %s; Gender: %s; Product ID: %s Duration: %sms",
						snapshotDetailTo.getBrandName(), snapshotDetailTo.getCategoryName(), snapshotDetailTo.getGenderName(),
						product.getProductId(), System.currentTimeMillis() - startTime));
//				existingProducts.get(dataProductId).setSnapshotDetail(product.getSnapshotDetail());
//				allProducts.getProducts().put(dataProductId, existingProducts.get(dataProductId));
				snapshotDetailTo.setProductId(existingProducts.get(dataProductId).getId());
			}
		}

		long endTime = System.currentTimeMillis();
		logger.info(String.format("Took %s ms to insert %s products from DB", endTime - startTime,
				allProducts.getSnapshotDetail().size() - existingProducts.size()));
	}
	
	private void setBrandInfo(SnapshotTo snapshotTo) {
		genderNames = new HashSet<String>();
		categoryNames = new HashSet<String>();
		productDataIds = new HashSet<String>();
		for(SnapshotDetailTo snapshotDetailTo : snapshotTo.getSnapshotDetail().values()) {
			if (brandName == null) { 
				brandName = snapshotDetailTo.getBrandName().toLowerCase();
			}
			
			genderNames.add(snapshotDetailTo.getGenderName().toLowerCase());
			categoryNames.add(snapshotDetailTo.getCategoryName().toLowerCase());
			productDataIds.add(snapshotDetailTo.getProductDataId());
		}
	}
	
	private SnapshotTo getSnapshotTo(ProductsTo products) {
		SnapshotTo snapshotTo = new SnapshotTo();
		snapshotTo.setSnapshotDetail(new HashMap<Long, SnapshotDetailTo>());
		snapshotTo.setBrandId(products.getBrand().getId());
		for(Product product : products.getProducts().values()) {
			SnapshotDetailTo snapshotDetailTo = new SnapshotDetailTo();
			snapshotDetailTo.setProductId(product.getId());
			snapshotDetailTo.setPriceDiscount(product.getSnapshotDetail().get(0).getPriceDiscount());
			snapshotDetailTo.setPriceRegular(product.getSnapshotDetail().get(0).getPriceRegular());
			
			snapshotTo.getSnapshotDetail().put(product.getId(), snapshotDetailTo);
		}
		
		return snapshotTo;
	}
	
	private void addToSnapshotQueue() {
		SnapshotTo snapshotTo = new SnapshotTo();
		snapshotTo.setSnapshotDetail(new HashMap<Long, SnapshotDetailTo>());
		snapshotTo.setBrandId(allProducts.getBrandId());
		for(SnapshotDetailTo snapshotDetailTo : allProducts.getSnapshotDetail().values()) {
			snapshotTo.getSnapshotDetail().put(snapshotDetailTo.getProductId(), snapshotDetailTo);
		}
		
		snapshotQueue.add(snapshotTo);
	}
}
