package com.sales.af.bo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.GenerationType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Repository;

@Entity
@Table(name = "genders")
@Repository
public class Gender implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5113675331957111510L;

	@Id
	@SequenceGenerator(name = "SEQ_GENDER_ID", allocationSize = 1, sequenceName = "SEQ_GENDER_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GENDER_ID")
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

	public Gender() {
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
