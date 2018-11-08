package com.apptivedeals.monitor.to;


public class Category extends AbstractEntity {
	
	@Override
	public String toString() {
		return "Category [id=" + getId()
				+ ", name=" + getName()
				+ ", createDate=" + getCreateDate()
				+ ", updateDate=" + getUpdateDate()
				+ ", isActive=" + getIsActive()
				+ "]";
	}
}
