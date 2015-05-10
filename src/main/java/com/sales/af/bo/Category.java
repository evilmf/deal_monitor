package com.sales.af.bo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;

@Entity
@Table(name = "categories")
@Repository
public class Category implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5732333556571155534L;

	@Id
	@SequenceGenerator(name = "SEQ_CATEGORY_ID", allocationSize = 1, sequenceName = "SEQ_CATEGORY_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CATEGORY_ID")
	private long id;

	@Column
	private String name;

	@Column(name = "create_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@Column(name = "update_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	@Column(name = "is_active")
	private boolean isActive;

	public Category() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	@PrePersist
	void onCreate() {
		setCreateDate(new Date());
		setUpdateDate(new Date());
		setActive(true);
	}

	@PreUpdate
	void updatedAt() {
		setUpdateDate(new Date());
	}
}
