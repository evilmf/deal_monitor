package com.sales.af.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sales.af.bo.Brand;
import com.sales.af.bo.Image;
import com.sales.af.bo.Product;
import com.sales.af.dao.ProductDao;
import com.sales.af.dao.UtilDao;
import com.sales.af.to.SearchCriteria;

@Repository
public class ProductDaoImpl implements ProductDao {
	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	UtilDao utilDao;
	
	private static Logger logger = Logger.getLogger(ProductDaoImpl.class);
	
	public Map<String, Product> getProductByProdIdAndBrandId(
			List<String> productIds, Long brandId) {
		Query query = entityManager.createNamedQuery(
				"getProductByProdIdAndBrand", Product.class);
		Brand brand = new Brand();
		brand.setId(brandId);
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
	
	public Object[] searchProduct(SearchCriteria searchCriteria) {
		Query query = entityManager.createNamedQuery("searchProductByName");
		String queryString = query.unwrap(org.hibernate.Query.class).getQueryString(); 
		queryString += searchCriteria.getMinPrice() == null ? "" : " and sd.price_discount >= ? "; 
		queryString += searchCriteria.getMaxPrice() == null ? "" : " and sd.price_discount <= ? "; 
		queryString += " order by ts_rank desc"; 
		
		logger.info(queryString);
		
		query = entityManager.createNativeQuery(queryString);
		
		int parameterPosition = 0;
		query.setParameter(++parameterPosition, searchCriteria.getSearchKeyword());
		query.setParameter(++parameterPosition, searchCriteria.getSearchKeyword());
		
		if(searchCriteria.getMinPrice() != null) {
			query.setParameter(++parameterPosition, searchCriteria.getMinPrice());
		}
		
		if(searchCriteria.getMaxPrice() != null) {
			query.setParameter(++parameterPosition, searchCriteria.getMaxPrice());
		}
		
		return query.getResultList().toArray();
	}
}
