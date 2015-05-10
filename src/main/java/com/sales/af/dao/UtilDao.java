package com.sales.af.dao;

import java.io.Serializable;

public interface UtilDao {
	public <T extends Serializable> T saveOrUpdate(T entity);

	public <T extends Serializable> T delete(T entity);

	public <T extends Serializable> T get(Long id, Class<T> genericType);

	public <T extends Iterable<Serializable>> void saveOrUpdateList(T entity);

	public void flushEntityManger();

	public <T extends Object> T getObj(Long id, Class<T> genericType);
}
