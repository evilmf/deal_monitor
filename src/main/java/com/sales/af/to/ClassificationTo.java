package com.sales.af.to;

import java.util.HashMap;
import java.util.Map;

public class ClassificationTo {
	private Map<Long, String> brands;
	private Map<Long, String> categories;
	private Map<Long, String> genders;
	
	public ClassificationTo() {
		this.categories = new HashMap<Long, String>();
		this.genders = new HashMap<Long, String>();
		this.brands = new HashMap<Long, String>();
	}
	
	public Map<Long, String> getBrands() {
		return brands;
	}
	public void setBrands(Map<Long, String> brands) {
		this.brands = brands;
	}
	public Map<Long, String> getCategories() {
		return categories;
	}
	public void setCategories(Map<Long, String> categories) {
		this.categories = categories;
	}
	public Map<Long, String> getGenders() {
		return genders;
	}
	public void setGenders(Map<Long, String> genders) {
		this.genders = genders;
	}
}
