package com.sales.af.bo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
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
@Table(name = "snapshot_detail")
public class SnapshotDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8691079796830130479L;

	@Id
	@SequenceGenerator(name = "SEQ_SNAPSHOT_DETAIL_ID", allocationSize = 1, sequenceName = "SEQ_SNAPSHOT_DETAIL_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SNAPSHOT_DETAIL_ID")
	private long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(name = "price_regular")
	private float priceRegular;

	@Column(name = "price_discount")
	private float priceDiscount;

	@Column(name = "snapshot_id")
	private long snapshotId;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "create_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	public SnapshotDetail() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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

	public Boolean isActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@PrePersist
	void onCreate() {
		setCreateDate(new Date());
		if (this.isActive == null) {
			setIsActive(true);
		}
	}
}
