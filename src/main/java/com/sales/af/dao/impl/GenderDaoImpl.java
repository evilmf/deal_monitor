package com.sales.af.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sales.af.bo.Gender;
import com.sales.af.dao.GenderDao;
import com.sales.af.dao.UtilDao;

@Repository
public class GenderDaoImpl implements GenderDao {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	UtilDao utilDao;
	
	@Override
	public Gender getGenderByName(String genderName) {
		Query query = entityManager.createNamedQuery("getGenderByName",
				Gender.class);
		query.setParameter("name", genderName);

		@SuppressWarnings("unchecked")
		List<Gender> genders = query.getResultList();

		if (genders.isEmpty()) {
			return null;
		} else {
			return genders.get(0);
		}
	}
	
	@Override
	public Gender insertGender(Gender gender) {
		return utilDao.saveOrUpdate(gender);
	}
	
	@Override
	public Gender insertGender(String genderName) {
		Gender gender = new Gender();
		gender.setName(genderName);
		
		return utilDao.saveOrUpdate(gender);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Gender> getGenders() {
		Query query = entityManager.createNamedQuery("getGenders",
				Gender.class);
		
		return query.getResultList();
	}
}
