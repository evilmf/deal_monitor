package com.sales.af.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sales.af.bo.Brand;
import com.sales.af.bo.Image;
import com.sales.af.bo.Product;
import com.sales.af.dao.ProductDao;
import com.sales.af.dao.UtilDao;

@Repository
public class ProductDaoImpl implements ProductDao {
	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	UtilDao utilDao;

	public Map<String, Product> getProductByProdIdAndBrandId(
			List<String> productIds, Brand brand) {
		Query query = entityManager.createNamedQuery(
				"getProductByProdIdAndBrand", Product.class);
		query.setParameter("productIds", productIds);
		query.setParameter("brand", brand);

		@SuppressWarnings("unchecked")
		List<Product> productList = query.getResultList();
		Map<String, Product> products = new HashMap<String, Product>();

		for (Product p : productList) {
			products.put(p.getProductId(), p);
		}
		return products;
	}

	public Product insertProduct(Product product) {
		for (Image i : product.getImages()) {
			i.setProduct(product);
		}

		return utilDao.saveOrUpdate(product);
	}
}
