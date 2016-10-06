package com.sales.af.service;

import java.util.List;

import com.sales.af.to.ProductSearchResultTo;
import com.sales.af.to.ProductSnapshots;
import com.sales.af.to.SearchCriteria;

public interface ProductSearchService {
	public List<ProductSearchResultTo> searchProduct(SearchCriteria searchCriteria);

	public ProductSnapshots getProductSnapshots(Long productId);
}
