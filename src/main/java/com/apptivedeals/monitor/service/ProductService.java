package com.apptivedeals.monitor.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apptivedeals.monitor.dao.BrandDao;
import com.apptivedeals.monitor.dao.CategoryDao;
import com.apptivedeals.monitor.dao.GenderDao;
import com.apptivedeals.monitor.dao.ProductDao;
import com.apptivedeals.monitor.to.Brand;
import com.apptivedeals.monitor.to.Category;
import com.apptivedeals.monitor.to.Classification;
import com.apptivedeals.monitor.to.Gender;
import com.apptivedeals.monitor.to.ProductSearchResult;
import com.apptivedeals.monitor.to.ProductSnapshots;
import com.apptivedeals.monitor.to.SearchCriteria;

@Service
public class ProductService {
	
	@Autowired
	private BrandDao brandDao;

	@Autowired
	private GenderDao genderDao;

	@Autowired
	private CategoryDao categoryDao;
	
	@Autowired
	private ProductDao productDao;
	
	private static Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
	
	public Map<Long, Brand> getBrands() {
		return brandDao.getBrands();
	}

	public Map<Long, Gender> getGenders() {
		return genderDao.getGenders();
	}

	public Map<Long, Category> getCategories() {
		return categoryDao.getCategories();
	}

	public Classification getClassification() {
		Classification classification = new Classification();

		Map<Long, String> brandNames = brandDao.getBrandNames();
		classification.setBrands(brandNames);

		Map<Long, String> genderNames = genderDao.getGenderNames();
		classification.setGenders(genderNames);

		Map<Long, String> categoryNames = categoryDao.getCategoryNames();
		classification.setCategories(categoryNames);

		return classification;
	}

	public List<ProductSearchResult> searchProduct(SearchCriteria searchCriteria) {
		searchCriteria.setSearchKeyword(parsedSearchKeyword(searchCriteria.getSearchKeyword()));
		LOGGER.info(String.format("Start searching for product with criteria: %s", searchCriteria.toString()));
		
		List<ProductSearchResult> searchResult = productDao.searchProduct(searchCriteria);
		
		return searchResult;
	}
	
	public ProductSnapshots getProductSnapshots(Long productId) {
		return productDao.getProductSnapshots(productId);
	}
	
	private String escapeForSearch(String keyword) {
		return keyword.replace("*", "\\*")
				.replace(":", "\\:")
				.replace("&", "\\&")
				.replaceAll("\\s+", " ")
				.trim();
	}
	
	private String parsedSearchKeyword(String keyword) {
		String[] words = escapeForSearch(keyword).split(" ");
		for(int i = 0; i < words.length; i++) {
			words[i] = words[i] + ":*";
		}
		return joinString(words, " & ");
	}
	
	private String joinString(String[] words, String separator) { 
		if (words == null) {
			return null;
		}
		
		if (separator == null) {
			separator = "";
		}
		
		String joinedWords = "";
		for (int i = 0; i < words.length; i++) {
			joinedWords += words[i];
			
			if (i < words.length - 1) {
				joinedWords += separator;
			}
		} 
		
		return joinedWords;
	}
}
