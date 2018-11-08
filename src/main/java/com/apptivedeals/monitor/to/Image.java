package com.apptivedeals.monitor.to;

import java.util.Date;

public class Image {
	
	private Long id;
	private String url;
	private Long productId;
	private Date createDate;
	private Date updateDate;
	private boolean active;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
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
		return "Image [id=" + id
				+ ", url=" + url
				+ ", productId=" + productId
				+ ", createDate=" + createDate
				+ ", updateDate=" + updateDate
				+ ", active=" + active
				+ "]";
	}
}
