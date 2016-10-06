package com.sales.af.service.impl;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sales.af.dao.ProductDao;
import com.sales.af.dao.SnapshotDetailDao;
import com.sales.af.service.ProductSearchService;
import com.sales.af.to.ProductSearchResultTo;
import com.sales.af.to.ProductSnapshot;
import com.sales.af.to.ProductSnapshots;
import com.sales.af.to.SearchCriteria;
import com.sales.af.util.Util;

@Service
public class ProductSearchServiceImpl implements ProductSearchService {
	@Autowired
	ProductDao productDao;
	
	@Autowired
	SnapshotDetailDao snapshotDetailDao;

	private static Logger logger = Logger.getLogger(ProductSearchServiceImpl.class);

	public List<ProductSearchResultTo> searchProduct(SearchCriteria searchCriteria) {
		
		searchCriteria.setSearchKeyword(Util.parsedSearchKeyword(searchCriteria.getSearchKeyword()));
		
		logger.info(String.format("Start searching for product with criteria: %s", searchCriteria.toString()));
		Object[] result = productDao.searchProduct(searchCriteria);
		List<ProductSearchResultTo> searchResultList = new ArrayList<ProductSearchResultTo>(result.length);
		
		Object[] data;
		ProductSearchResultTo searchResult;
		for (Object res : result) {
			data = (Object[]) res;
			searchResult = new ProductSearchResultTo();
			
			searchResult.setProductId(Long.parseLong(data[0].toString()));
			searchResult.setProductName(data[1].toString());
			searchResult.setBrandName(data[2].toString());
			searchResult.setGenderName(data[3].toString());
			searchResult.setCategoryName(data[4].toString());
			searchResult.setImageUrl(data[5].toString());
			searchResult.setMinPrice(Float.parseFloat(data[6].toString()));
			searchResult.setMaxPrice(Float.parseFloat(data[7].toString()));
			searchResult.setSnapshotCount(Integer.parseInt(data[8].toString()));
			searchResult.setScore(Float.parseFloat(data[9].toString()));
			
			searchResultList.add(searchResult);
		}

		return searchResultList;
	}
	
	public ProductSnapshots getProductSnapshots(Long productId) {
		ProductSnapshots productSnapshots = new ProductSnapshots();
		
		logger.info(String.format("Getting product snapshots by product id: %s", productId));
		Object[] result = snapshotDetailDao.getSnapshotListByProductId(productId);
		
		Object[] data;
		ProductSnapshot productSnapshot;
		for(Object res : result) {
			data = (Object[]) res;
			productSnapshot = new ProductSnapshot();
			
			productSnapshot.setSnapshotId(Long.parseLong(data[0].toString()));
			productSnapshot.setProductId(Long.parseLong(data[1].toString()));
			productSnapshot.setPrice(Float.parseFloat(data[9].toString()));
			productSnapshot.setActiveDate(new Date(((Timestamp) data[10]).getTime()));
			productSnapshot.setInactiveDate(new Date(((Timestamp) data[11]).getTime()));
			productSnapshot.setDuration(Float.parseFloat((data[12].toString())));
			
			productSnapshots.setProductName(data[2].toString());
			productSnapshots.setBrandId(Long.parseLong(data[3].toString()));
			productSnapshots.setBrand(data[4].toString());
			productSnapshots.setGenderId(Long.parseLong(data[5].toString()));
			productSnapshots.setGender(data[6].toString());
			productSnapshots.setCategoryId(Long.parseLong(data[7].toString()));
			productSnapshots.setCategory(data[8].toString());
			
			productSnapshots.getProductSnapshotList().add(productSnapshot);
		}
		
		return productSnapshots;
	}
}
