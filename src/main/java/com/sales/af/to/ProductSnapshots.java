package com.sales.af.to;

import java.util.ArrayList;
import java.util.List;

public class ProductSnapshots {
	private Long brandId;
	private String brand;
	private Long genderId;
	private String gender;
	private Long categoryId;
	private String category;
	private String productName;
	private List<ProductSnapshot> productSnapshotList;
	
	public ProductSnapshots() {
		setProductSnapshotList(new ArrayList<ProductSnapshot>());
	}
	
	public Long getBrandId() {
		return brandId;
	}
	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public Long getGenderId() {
		return genderId;
	}
	public void setGenderId(Long genderId) {
		this.genderId = genderId;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public List<ProductSnapshot> getProductSnapshotList() {
		return productSnapshotList;
	}
	public void setProductSnapshotList(List<ProductSnapshot> productSnapshotList) {
		this.productSnapshotList = productSnapshotList;
	}
}
