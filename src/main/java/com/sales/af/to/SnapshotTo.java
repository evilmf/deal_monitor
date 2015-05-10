package com.sales.af.to;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.sales.af.bo.Snapshot;

public class SnapshotTo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1364801262312581816L;
    private Long snapshotId;
    private Date createDate;
    private Date updateDate;
    private Boolean isActive;
    private Long brandId;
    Map<Long, SnapshotDetailTo> snapshotDetail;

    public SnapshotTo() {
    }

    public SnapshotTo(Snapshot snapshot) {
	this.snapshotId = snapshot.getId();
	this.createDate = snapshot.getCreateDate();
	this.updateDate = snapshot.getUpdateDate();
	this.isActive = snapshot.getIsActive();
	this.brandId = snapshot.getBrand() != null ? snapshot.getBrand()
		.getId() : null;
	this.snapshotDetail = null;
    };
    
    public Map<Long, SnapshotDetailTo> getSnapshotDetail() {
	return this.snapshotDetail;
    }
    
    public void setSnapshotDetail(Map<Long, SnapshotDetailTo> snapshot) {
	this.snapshotDetail = snapshot;
    }

    public Boolean getIsActive() {
	return isActive;
    }

    public void setIsActive(Boolean isActive) {
	this.isActive = isActive;
    }

    public Long getBrandId() {
	return brandId;
    }

    public void setBrandId(Long brandId) {
	this.brandId = brandId;
    }

    public Date getUpdateDate() {
	return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
	this.updateDate = updateDate;
    }

    public Date getCreateDate() {
	return createDate;
    }

    public void setCreateDate(Date createDate) {
	this.createDate = createDate;
    }

    public Long getSnapshotId() {
	return snapshotId;
    }

    public void setSnapshotId(Long snapshotId) {
	this.snapshotId = snapshotId;
    }

}
