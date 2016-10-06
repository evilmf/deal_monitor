package com.sales.af.to;

public class ProductSearchResultTo {
	private Long productId;
	private String productName;
	private String brandName;
	private String genderName;
	private String categoryName;
	private String imageUrl;
	private float minPrice;
	private float maxPrice;
	private int snapshotCount;
	private float score;
	
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getBrandName() {
		return brandName;
	}
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public float getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(float minPrice) {
		this.minPrice = minPrice;
	}
	public float getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(float maxPrice) {
		this.maxPrice = maxPrice;
	}
	public int getSnapshotCount() {
		return snapshotCount;
	}
	public void setSnapshotCount(int snapshotCount) {
		this.snapshotCount = snapshotCount;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	public String getGenderName() {
		return genderName;
	}
	public void setGenderName(String genderName) {
		this.genderName = genderName;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
