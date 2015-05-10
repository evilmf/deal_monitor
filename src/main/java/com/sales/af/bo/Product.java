package com.sales.af.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "products")
public class Product implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1156235935400521461L;

	@Id
	@SequenceGenerator(name = "SEQ_PRODUCT_ID", allocationSize = 1, sequenceName = "SEQ_PRODUCT_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PRODUCT_ID")
	private long id;

	@Column(name = "product_id")
	private String productId;

	@OneToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@OneToOne
	@JoinColumn(name = "brand_id")
	private Brand brand;

	@OneToOne
	@JoinColumn(name = "gender_id")
	private Gender gender;

	@Column
	private String name;

	@Column(name = "url")
	private String productUrl;

	@Column(name = "create_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@Column(name = "update_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	@Column(name = "is_active")
	private boolean isActive;

	@Column(name = "count")
	private long count;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Image> images = new ArrayList<Image>();

	@OneToMany(mappedBy = "product")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<SnapshotDetail> snapshotDetail = new ArrayList<SnapshotDetail>();

	public Product() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
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

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public List<SnapshotDetail> getSnapshotDetail() {
		return snapshotDetail;
	}

	public void setSnapshotDetail(List<SnapshotDetail> snapshotDetail) {
		this.snapshotDetail = snapshotDetail;
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
