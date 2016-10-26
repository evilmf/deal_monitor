package com.sales.af.dao;

import java.util.List;
import java.util.Map;

import com.sales.af.bo.Product;
import com.sales.af.to.SearchCriteria;

public interface ProductDao {
	public Map<String, Product> getProductByProdIdAndBrandId(List<String> productIds, Long brandId);
	
	public Map<String, Product> getProductByBrandId(Long brandId);

	public Product insertProduct(Product product);
	
	public Object[] searchProduct(SearchCriteria searchCriteria);
	
	public boolean updateProductUrl(Long pid, String url);
}
