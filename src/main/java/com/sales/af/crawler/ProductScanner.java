package com.sales.af.crawler;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sales.af.to.SnapshotDetailTo;

public class ProductScanner {
	private final static Logger LOGGER = Logger.getLogger(ProductScanner.class);
	
	@Autowired
	ProductQueueService productQueueService;
	
	@Autowired
	Crawler afCrawler;
	
	@Autowired
	Crawler holCrawler;
	
	public void scan() {
		LOGGER.info("Start scanning for deal...");
		Set<SnapshotDetailTo> products = new HashSet<SnapshotDetailTo>();
		
		products.addAll(afCrawler.getProducts());
		products.addAll(holCrawler.getProducts());
		
		LOGGER.info(String.format("Total number of products: %s", products.size()));
		
		
		if(!products.isEmpty()) {
			productQueueService.addProducts(products);
		}
		else {
			LOGGER.warn("No product is found!");
		}
		
		LOGGER.info(String.format("Done scanning! Product Queue size: %s.", productQueueService.productQueueSize()));
	}
}
