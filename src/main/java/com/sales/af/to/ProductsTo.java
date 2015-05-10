package com.sales.af.to;

import java.util.HashMap;
import java.util.Map;  

import com.sales.af.bo.Brand;
import com.sales.af.bo.Category;
import com.sales.af.bo.Gender;
import com.sales.af.bo.Product;

public class ProductsTo {
	private Brand brand;
	private Map<String, Category> categories;
	private Map<String, Gender> genders;
	private Map<String/* Product Id */, Product> products;

	public ProductsTo() {
		this.brand = new Brand();
		this.categories = new HashMap<String, Category>();
		this.genders = new HashMap<String, Gender>();
		this.products = new HashMap<String, Product>();
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public Map<String, Category> getCategories() {
		return categories;
	}

	public void setCategories(Map<String, Category> categories) {
		this.categories = categories;
	}

	public Map<String, Gender> getGenders() {
		return genders;
	}

	public void setGenders(Map<String, Gender> genders) {
		this.genders = genders;
	}

	public Map<String, Product> getProducts() {
		return products;
	}

	public void setProducts(Map<String, Product> products) {
		this.products = products;
	}

	public int getProductCount() {
		return products.size();
	}

	public int getGetCategoryCount() {
		return categories.size();
	}
}
