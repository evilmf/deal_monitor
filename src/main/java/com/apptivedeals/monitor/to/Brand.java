package com.apptivedeals.monitor.to;


public class Brand extends AbstractEntity {
	
	@Override
	public String toString() {
		return "Brand [id=" + getId()
				+ ", name=" + getName()
				+ ", createDate=" + getCreateDate()
				+ ", updateDate=" + getUpdateDate()
				+ ", isActive=" + getIsActive()
				+ "]";
	}
}
