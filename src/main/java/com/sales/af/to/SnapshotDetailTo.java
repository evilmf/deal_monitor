package com.sales.af.to;

import java.util.Date;
import java.util.List;

public class SnapshotDetailTo {
	private Long snapshotDetailId;

	private Long productId;

	private String productDataId;

	private Date productCreateDate;

	private Long brandId;

	private String brandName;

	private Long genderId;

	private String genderName;

	private Long categoryId;

	private String categoryName;

	private Long snapshotId;

	private float priceRegular;

	private float priceDiscount;

	private float discount;

	private Boolean isNew;

	private Date snapshotCreateDate;

	private List<String> images;

	private String productUrl;

	private String productName;

	public Long getSnapshotDetailId() {
		return snapshotDetailId;
	}

	public void setSnapshotDetailId(Long snapshotDetailId) {
		this.snapshotDetailId = snapshotDetailId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductDataId() {
		return productDataId;
	}

	public void setProductDataId(String productDataId) {
		this.productDataId = productDataId;
	}

	public Date getProductCreateDate() {
		return productCreateDate;
	}

	public void setProductCreateDate(Date productCreateDate) {
		this.productCreateDate = productCreateDate;
	}

	public Long getBrandId() {
		return brandId;
	}

	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public Long getGenderId() {
		return genderId;
	}

	public void setGenderId(Long genderId) {
		this.genderId = genderId;
	}

	public String getGenderName() {
		return genderName;
	}

	public void setGenderName(String genderName) {
		this.genderName = genderName;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Long getSnapshotId() {
		return snapshotId;
	}

	public void setSnapshotId(Long snapshotId) {
		this.snapshotId = snapshotId;
	}

	public float getPriceRegular() {
		return priceRegular;
	}

	public void setPriceRegular(float priceRegular) {
		this.priceRegular = priceRegular;
	}

	public float getPriceDiscount() {
		return priceDiscount;
	}

	public void setPriceDiscount(float priceDiscount) {
		this.priceDiscount = priceDiscount;
	}

	public Boolean getIsNew() {
		return isNew;
	}

	public void setIsNew(Boolean isNew) {
		this.isNew = isNew;
	}

	public Date getSnapshotCreateDate() {
		return snapshotCreateDate;
	}

	public void setSnapshotCreateDate(Date snapshotCreateDate) {
		this.snapshotCreateDate = snapshotCreateDate;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public void addImage(String url) {
		this.images.add(url);
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
}
