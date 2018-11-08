package com.apptivedeals.monitor.crawler;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.apptivedeals.monitor.to.Snapshot.SnapshotDetail;

@Component
public class ProductScanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductScanner.class);

	@Autowired
	private ProductQueueService productQueueService;

	@Autowired
	private Crawler afCrawler;
	
	@Autowired
	private Crawler hollisterCrawler;

	@Scheduled(fixedDelay = 300000)
	public void scan() {
		LOGGER.info("Start scanning for deal...");
		Set<SnapshotDetail> products = new HashSet<SnapshotDetail>();

		products.addAll(afCrawler.getProducts());
		products.addAll(hollisterCrawler.getProducts());

		LOGGER.info(String.format("Total number of products: %s", products.size()));

		if (!products.isEmpty()) {
			productQueueService.addProducts(products);
		} else {
			LOGGER.warn("No product is found!");
		}

		LOGGER.info(String.format("Done scanning! Product Queue size: %s.", productQueueService.productQueueSize()));

	}
}
