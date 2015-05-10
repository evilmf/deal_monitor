package com.sales.af.bo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "images")
public class Image implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5895570707025570348L;

	@Id
	@SequenceGenerator(name = "SEQ_IMAGE_ID", allocationSize = 1, sequenceName = "SEQ_IMAGE_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_IMAGE_ID")
	private long id;

	@Column(name = "url")
	private String imageUrl;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(name = "create_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@Column(name = "update_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	@Column(name = "is_active")
	private Boolean isActive;

	public Image() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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

	public Boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@PrePersist
	void onCreate() {
		setCreateDate(new Date());
		setUpdateDate(new Date());
		if (this.isActive == null) {
			setActive(true);
		}
	}

	@PreUpdate
	void updatedAt() {
		setUpdateDate(new Date());
	}
}
