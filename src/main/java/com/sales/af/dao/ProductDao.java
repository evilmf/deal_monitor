package com.sales.af.dao;

import java.util.List;
import java.util.Map;

import com.sales.af.bo.Brand;
import com.sales.af.bo.Product;

public interface ProductDao {
	public Map<String, Product> getProductByProdIdAndBrandId(
			List<String> productIds, Brand brand);

	public Product insertProduct(Product product);
}
