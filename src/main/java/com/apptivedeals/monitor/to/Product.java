package com.apptivedeals.monitor.to;

import java.util.Date;

public class Product {
	private long id;
	private String productId;
	private long categoryId;
	private long brandId;
	private long genderId;
	private String name;
	private String url;
	private Date createDate;
	private Date updateDate;
	private boolean active;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	public long getBrandId() {
		return brandId;
	}
	public void setBrandId(long brandId) {
		this.brandId = brandId;
	}
	public long getGenderId() {
		return genderId;
	}
	public void setGenderId(long genderId) {
		this.genderId = genderId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public String toString() {
		return "Product [active=" + active
				+ ", brandId=" + brandId
				+ ", categoryId=" + categoryId
				+ ", createDate=" + createDate
				+ ", genderId=" + genderId
				+ ", id=" + id
				+ ", name=" + name
				+ ", productId=" + productId
				+ ", updateDate=" + updateDate
				+ ", url=" + url
				+ "]";
	}
}
