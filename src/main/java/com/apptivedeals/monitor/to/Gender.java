package com.apptivedeals.monitor.to;


public class Gender extends AbstractEntity {
	
	@Override
	public String toString() {
		return "Gender [id=" + getId()
				+ ", name=" + getName()
				+ ", createDate=" + getCreateDate()
				+ ", updateDate=" + getUpdateDate()
				+ ", isActive=" + getIsActive()
				+ "]";
	}
}
