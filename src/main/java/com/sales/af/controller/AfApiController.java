package com.sales.af.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sales.af.service.DealApiService;
import com.sales.af.to.ClassificationTo;
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

    private static Logger logger = Logger.getLogger(AfApiController.class);

    @RequestMapping(value = "/getSnapshot/{snapshotId}", method = RequestMethod.GET)
    public @ResponseBody
    SnapshotTo getSnapshot(@PathVariable Long snapshotId) {
		logger.info(String
			.format("Rest API Called: getSnapshot/%s", snapshotId));
	
		return dealApiService.getSnapshotDetailById(snapshotId);
    }

    @RequestMapping(value = "/getSnapshot", method = RequestMethod.GET)
    public @ResponseBody
    SnapshotTo getSnapshot() {
		logger.info("Rest API Called: getSnapshot");
	
		return dealApiService.getLatestSnapshotDetail();
    }

    @RequestMapping(value = "/getSnapshotNoDetail", method = RequestMethod.GET)
    public @ResponseBody
    SnapshotTo getLatestSnapshotWithoutDetail() {
		logger.info("Rest API called: getSnapshotNoDetail");
	
		return dealApiService.getSnapshotNoDetail();
    }
    
    @RequestMapping(value = "/getClassification", method = RequestMethod.GET)
    public @ResponseBody
    ClassificationTo getClassification() {
		logger.info("Rest API called: getClassification");
	
		return dealApiService.getClassification();
    }
}