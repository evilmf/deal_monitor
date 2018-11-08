package com.apptivedeals.monitor.to;

import java.util.Date;

public class SnapshotDetail {
	
	private long id;
	private long productId;
	private float priceRegular;
	private float priceDiscount;
	private long snapshotId;
	private boolean isActive;
	private Date createDate;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getProductId() {
		return productId;
	}
	public void setProductId(long productId) {
		this.productId = productId;
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
	public long getSnapshotId() {
		return snapshotId;
	}
	public void setSnapshotId(long snapshotId) {
		this.snapshotId = snapshotId;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	
}