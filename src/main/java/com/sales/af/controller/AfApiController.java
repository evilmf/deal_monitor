package com.sales.af.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sales.af.service.DealApiService;
import com.sales.af.service.ProductSearchService;
import com.sales.af.to.ClassificationTo;
import com.sales.af.to.ProductSearchResultTo;
import com.sales.af.to.ProductSnapshots;
import com.sales.af.to.SearchCriteria;
import com.sales.af.to.SnapshotTo;

/**
 * 
 * @author jlong
 * 
 **/

@Controller
public class AfApiController {

	@Autowired
	DealApiService dealApiService;
	
	@Autowired
	ProductSearchService productSearchService;

	private static Logger logger = Logger.getLogger(AfApiController.class);

	@RequestMapping(value = "/getSnapshot/{snapshotId}", method = RequestMethod.GET)
	public @ResponseBody SnapshotTo getSnapshot(@PathVariable Long snapshotId) {
		logger.info(String.format("Rest API Called: getSnapshot/%s", snapshotId));

		return dealApiService.getSnapshotDetailById(snapshotId);
	}

	@RequestMapping(value = "/getSnapshot", method = RequestMethod.GET)
	public @ResponseBody SnapshotTo getSnapshot() {
		logger.info("Rest API Called: getSnapshot");

		return dealApiService.getLatestSnapshotDetail();
	}

	@RequestMapping(value = "/getSnapshotNoDetail", method = RequestMethod.GET)
	public @ResponseBody SnapshotTo getLatestSnapshotWithoutDetail() {
		logger.info("Rest API called: getSnapshotNoDetail");

		return dealApiService.getSnapshotNoDetail();
	}

	@RequestMapping(value = "/getClassification", method = RequestMethod.GET)
	public @ResponseBody ClassificationTo getClassification() {
		logger.info("Rest API called: getClassification");

		return dealApiService.getClassification();
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public @ResponseBody List<ProductSearchResultTo> getSearchResult(@Valid @RequestBody SearchCriteria searchCriteria) {
		logger.info("Rest API called: getSearchResult");
		
		return productSearchService.searchProduct(searchCriteria);
	}
	
	@RequestMapping(value = "/productSnapshots/{productId}", method = RequestMethod.GET)
	public @ResponseBody ProductSnapshots getProductSnapshots(@PathVariable Long productId) {
		logger.info("Rest API called: getProductSnapshots");
		
		return productSearchService.getProductSnapshots(productId);
	}
}