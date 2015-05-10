package com.sales.af.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

import com.sales.af.dao.UtilDao;

@Repository
public class UtilDaoImpl implements UtilDao {
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public <T extends Serializable> T saveOrUpdate(T entity) {
		return entityManager.merge(entity);
	}

	public <T extends Serializable> T delete(T entity) {
		entityManager.remove(entity);
		return entity;
	}

	public <T extends Serializable> T get(Long id, Class<T> genericType) {
		T bo = entityManager.find(genericType, id);
		return bo;
	}

	@Override
	public <T extends Object> T getObj(Long id, Class<T> genericType) {
		T bo = entityManager.find(genericType, id);
		return bo;
	}

	@Transactional
	public <T extends Iterable<Serializable>> void saveOrUpdateList(T entity) {
		for (Object obj : entity) {
			entityManager.merge(obj);
		}
	}

	@Override
	public void flushEntityManger() {
		entityManager.flush();
	}
}
