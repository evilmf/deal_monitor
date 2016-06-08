package com.sales.af.dao;

import java.util.List;
import java.util.Map;

import com.sales.af.bo.Brand;
import com.sales.af.bo.Product;

public interface ProductDao {
	public Map<String, Product> getProductByProdIdAndBrandId(List<String> productIds, Long brandId);

	public Product insertProduct(Product product);
}
