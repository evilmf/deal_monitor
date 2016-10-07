package com.sales.af.to;

import java.sql.Timestamp;
import java.util.Date;

public class ProductSnapshot {
	private Long snapshotId;
	private Long productId;
	private Float price;
	private Timestamp activeDate;
	private Timestamp inactiveDate;
	private Float duration;
	
	public Long getSnapshotId() {
		return snapshotId;
	}
	public void setSnapshotId(Long snapshotId) {
		this.snapshotId = snapshotId;
	}
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public Date getActiveDate() {
		return activeDate;
	}
	public void setActiveDate(Timestamp activeDate) {
		this.activeDate = activeDate;
	}
	public Date getInactiveDate() {
		return inactiveDate;
	}
	public void setInactiveDate(Timestamp inactiveDate) {
		this.inactiveDate = inactiveDate;
	}
	public Float getDuration() {
		return duration;
	}
	public void setDuration(Float duration) {
		this.duration = duration;
	}	
}
