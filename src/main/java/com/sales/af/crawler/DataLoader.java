package com.sales.af.crawler;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sales.af.bo.Brand;
import com.sales.af.bo.Category;
import com.sales.af.bo.Gender;
import com.sales.af.bo.Product;
import com.sales.af.dao.BrandDao;
import com.sales.af.dao.CategoryDao;
import com.sales.af.dao.GenderDao;
import com.sales.af.dao.ProductDao;
import com.sales.af.to.ProductsTo;

public class DataLoader extends ProductQueue {
    private static Logger logger = Logger.getLogger(DataLoader.class);
    private ProductsTo allProducts;

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
	if (allProducts != null && allProducts.getProductCount() > 0) {
	    long startTime = System.currentTimeMillis();
	    logger.info(String.format(
		    "Start Loading %s Products for Brand: %s; Queue Size: %s",
		    allProducts.getProductCount(), allProducts.getBrand()
			    .getName(), productQueue.size()));

	    loadBrand();
	    loadGender();
	    loadCategory();
	    loadProduct();

	    snapshotQueue.add(allProducts);

	    long endTime = System.currentTimeMillis();
	    logger.info(String
		    .format("Done Loading for Brand %s; Queue Size: %s; Duration: %s ms",
			    allProducts.getBrand().getName(),
			    productQueue.size(), endTime - startTime));
	}
    }

    private void loadBrand() {
	String brandName = allProducts.getBrand().getName().toLowerCase();
	if (existingBrand.containsKey(brandName)) {
	    logger.debug(String.format("Brand %s exists in memory.", brandName));
	    allProducts.getBrand().setId(existingBrand.get(brandName));

	} else {
	    logger.info(String.format("Loading brand %s into DB and memory.",
		    brandName));
	    Brand brand = brandDao.getBrandByName(brandName);

	    if (brand == null) {
		logger.info(String.format("Inserting brand %s into DB",
			brandName));
		allProducts.setBrand(brandDao.insertBrand(allProducts
			.getBrand()));
	    } else {
		logger.info(String.format("Brand %s already exists in the DB",
			brandName));
		allProducts.setBrand(brand);
	    }

	    existingBrand.put(brandName, allProducts.getBrand().getId());
	    logger.debug(String.format("Brand %s has id %s", brandName,
		    allProducts.getBrand().getId()));
	}
    }

    private void loadGender() {
	for (String genderName : allProducts.getGenders().keySet()) {
	    if (existingGender.containsKey(genderName.toLowerCase())) {
		logger.debug(String.format("Gender %s exists in memory.",
			genderName));
		allProducts.getGenders().get(genderName)
			.setId(existingGender.get(genderName.toLowerCase()));
	    } else {
		logger.info(String.format(
			"Loading gender %s into DB and memory", genderName));
		Gender gender = genderDao.getGenderByName(genderName
			.toLowerCase());

		if (gender == null) {
		    logger.debug(String.format("Insert gender %s into DB",
			    genderName));
		    allProducts.getGenders().put(
			    genderName,
			    genderDao.insertGender(allProducts.getGenders()
				    .get(genderName)));

		} else {
		    logger.debug(String.format(
			    "Gender %s already exists in the DB",
			    genderName.toLowerCase()));
		    allProducts.getGenders().put(genderName, gender);
		}

		existingGender.put(genderName.toLowerCase(), allProducts
			.getGenders().get(genderName).getId());
		logger.debug(String.format("Gender %s has id %s",
			genderName.toLowerCase(),
			allProducts.getGenders().get(genderName).getId()));
	    }

	}
    }

    private void loadCategory() {
	for (String categoryName : allProducts.getCategories().keySet()) {
	    if (existingCategory.containsKey(categoryName.toLowerCase())) {
		logger.debug(String.format("Category %s exists in memory.",
			categoryName));
		allProducts
			.getCategories()
			.get(categoryName)
			.setId(existingCategory.get(categoryName.toLowerCase()));
	    } else {
		logger.debug(String.format(
			"Loading category %s into DB and memory", categoryName));
		Category category = categoryDao.getCategoryByName(categoryName
			.toLowerCase());

		if (category == null) {
		    logger.debug(String.format("Insert category %s into DB",
			    categoryName));
		    allProducts.getCategories().put(
			    categoryName,
			    categoryDao.insertCategory(allProducts
				    .getCategories().get(categoryName)));

		} else {
		    logger.debug(String.format(
			    "Category %s already exists in the DB",
			    categoryName.toLowerCase()));
		    allProducts.getCategories().put(categoryName, category);
		}

		existingCategory.put(categoryName.toLowerCase(), allProducts
			.getCategories().get(categoryName).getId());
		logger.debug(String.format("Category %s has id %s",
			categoryName.toLowerCase(), allProducts.getCategories()
				.get(categoryName).getId()));
	    }
	}
    }

    private void loadProduct() {
	long startTime = System.currentTimeMillis();
	Map<String, Product> existingProducts = productDao
		.getProductByProdIdAndBrandId(new ArrayList<String>(allProducts
			.getProducts().keySet()), allProducts.getBrand());

	for (String dataProductId : allProducts.getProducts().keySet()) {
	    Product product = allProducts.getProducts().get(dataProductId);
	    if (!existingProducts.containsKey(dataProductId)) {
		product.getBrand().setId(allProducts.getBrand().getId());
		product.getGender().setId(
			existingGender.get(product.getGender().getName()
				.toLowerCase()));
		product.getCategory().setId(
			existingCategory.get(product.getCategory().getName()
				.toLowerCase()));
		product.setCount(1);

		Product p = productDao.insertProduct(product);
		p.getSnapshotDetail()
			.get(0)
			.setPriceDiscount(
				allProducts.getProducts().get(dataProductId)
					.getSnapshotDetail().get(0)
					.getPriceDiscount());
		p.getSnapshotDetail()
			.get(0)
			.setPriceRegular(
				allProducts.getProducts().get(dataProductId)
					.getSnapshotDetail().get(0)
					.getPriceRegular());
		allProducts.getProducts().put(dataProductId, p);

		logger.debug(String
			.format("Insert Product %s ID %s into DB; Category: %s; Gender: %s; Product ID: %s; List Price: %s; Offer Price: %s",
				p.getId(), p.getName(), p.getCategory()
					.getName(), p.getGender().getName(), p
					.getProductId(), p.getSnapshotDetail()
					.get(0).getPriceRegular(), p
					.getSnapshotDetail().get(0)
					.getPriceDiscount()));
	    } else {
		logger.debug(String
			.format("Product %s already exists in DB; Category: %s; Gender: %s; Product ID: %s Duration: %sms",
				product.getName(), product.getCategory()
					.getName(), product.getGender()
					.getName(), product.getProductId(),
				System.currentTimeMillis() - startTime));
		existingProducts.get(dataProductId).setSnapshotDetail(
			product.getSnapshotDetail());
		allProducts.getProducts().put(dataProductId,
			existingProducts.get(dataProductId));
	    }
	}

	long endTime = System.currentTimeMillis();
	logger.info(String.format("Took %s ms to insert %s products from DB",
		endTime - startTime, allProducts.getProductCount()
			- existingProducts.size()));
    }
}
