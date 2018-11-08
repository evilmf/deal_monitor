package com.apptivedeals.monitor.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.apptivedeals.monitor.service.ProductService;
import com.apptivedeals.monitor.to.Brand;
import com.apptivedeals.monitor.to.Category;
import com.apptivedeals.monitor.to.Classification;
import com.apptivedeals.monitor.to.Gender;
import com.apptivedeals.monitor.to.ProductSearchResult;
import com.apptivedeals.monitor.to.ProductSnapshots;
import com.apptivedeals.monitor.to.SearchCriteria;

@RestController
@RequestMapping("/service")
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	private static Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
	
	@RequestMapping(value = "/brands", method = RequestMethod.GET)
	public @ResponseBody Map<Long, Brand> getBrands() {
		LOGGER.info("Rest API called: getBrands");
		
		return productService.getBrands();
	}
	
	@RequestMapping(value = "/genders", method = RequestMethod.GET)
	public @ResponseBody Map<Long, Gender> getGenders() {
		LOGGER.info("Rest API called: getGenders");
		
		return productService.getGenders();
	}
	
	@RequestMapping(value = "/categories", method = RequestMethod.GET)
	public @ResponseBody Map<Long, Category> getCategories() {
		LOGGER.info("Rest API called: getCategories");
		
		return productService.getCategories();
	}
	
	@RequestMapping(value = "/classification", method = RequestMethod.GET)
	public @ResponseBody Classification getClassification() {
		LOGGER.info("Rest API called: getClassification");
		
		return productService.getClassification();
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public @ResponseBody List<ProductSearchResult> getSearchResult(@Valid @RequestBody SearchCriteria searchCriteria) {
		LOGGER.info("Rest API called: getSearchResult");
		
		return productService.searchProduct(searchCriteria);
	}
	
	@RequestMapping(value = "/productSnapshots/{productId}", method = RequestMethod.GET)
	public @ResponseBody ProductSnapshots getProductSnapshots(@PathVariable Long productId) {
		LOGGER.info("Rest API called: getProductSnapshots({})", productId);
		
		return productService.getProductSnapshots(productId);
	}
}
