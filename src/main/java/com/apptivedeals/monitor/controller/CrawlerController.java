package com.apptivedeals.monitor.controller;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.apptivedeals.monitor.crawler.Crawler;
import com.apptivedeals.monitor.to.Snapshot.SnapshotDetail;

@RestController
@RequestMapping("/service")
public class CrawlerController {
	
	@Autowired
	private Crawler afCrawler;
	
	private static Logger LOGGER = LoggerFactory.getLogger(CrawlerController.class);
	
	@RequestMapping(value = "/crawl", method = RequestMethod.GET)
	public @ResponseBody Set<SnapshotDetail> crawl() {
		LOGGER.info("Rest API called: crawl");
		
		return afCrawler.getProducts();
	}
}
