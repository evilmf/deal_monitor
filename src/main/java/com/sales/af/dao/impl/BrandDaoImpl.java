package com.sales.af.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sales.af.bo.Brand;
import com.sales.af.dao.BrandDao;
import com.sales.af.dao.UtilDao;

@Repository
public class BrandDaoImpl implements BrandDao {
	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	UtilDao utilDao;

	@Override
	public Brand getBrandByName(String brandName) {
		Query query = entityManager.createNamedQuery("getBrandByName", Brand.class);
		query.setParameter("name", brandName);

		@SuppressWarnings("unchecked")
		List<Brand> brands = query.getResultList();

		if (brands.isEmpty()) {
			return null;
		} else {
			return brands.get(0);
		}
	}

	@Override
	public Brand insertBrand(Brand brand) {
		return utilDao.saveOrUpdate(brand);
	}
	
	@Override
	public Brand insertBrand(String brandName) {
		Brand brand = new Brand();
		brand.setName(brandName);
		
		return utilDao.saveOrUpdate(brand);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Brand> getBrands() {
		Query query = entityManager.createNamedQuery("getBrands", Brand.class);

		return query.getResultList();
	}
}
