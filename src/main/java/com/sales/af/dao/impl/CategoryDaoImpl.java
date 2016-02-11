package com.sales.af.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sales.af.bo.Category;
import com.sales.af.dao.CategoryDao;
import com.sales.af.dao.UtilDao;

@Repository
public class CategoryDaoImpl implements CategoryDao {
	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	UtilDao utilDao;
	
	@Override
	public Category getCategoryByName(String categoryName) {
		Query query = entityManager.createNamedQuery("getCategoryByName",
				Category.class);
		query.setParameter("name", categoryName);

		@SuppressWarnings("unchecked")
		List<Category> categories = query.getResultList();

		if (categories.isEmpty()) {
			return null;
		} else {
			return categories.get(0);
		}
	}
	
	@Override
	public Category insertCategory(Category category) {
		return utilDao.saveOrUpdate(category);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Category> getCategories() {
		Query query = entityManager.createNamedQuery("getCategories",
				Category.class);
		
		return query.getResultList();
	}
}
