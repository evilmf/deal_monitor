package com.apptivedeals.monitor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.apptivedeals.monitor.service.SnapshotService;
import com.apptivedeals.monitor.to.Snapshot;

@RestController
@RequestMapping("/service")
public class SnapshotController {

	@Autowired
	private SnapshotService snapshotService;

	private static Logger LOGGER = LoggerFactory.getLogger(SnapshotController.class);

	@RequestMapping(value = "/snapshot/{snapshotId}", method = RequestMethod.GET)
	public @ResponseBody Snapshot getSnapshot(@PathVariable Long snapshotId) {
		LOGGER.info("Rest API Called: getSnapshot/{}", snapshotId);

		return snapshotService.getSnapshotById(snapshotId);
	}
	
	@RequestMapping(value = "/snapshot", method = RequestMethod.GET)
	public @ResponseBody Snapshot getSnapshot() {
		LOGGER.info("Rest API Called: getSnapshot");

		return snapshotService.getLatestSnapshot();
	}
	
	@RequestMapping(value = "/snapshotNoDetail", method = RequestMethod.GET)
	public @ResponseBody Snapshot getLatestSnapshotWithoutDetail() {
		LOGGER.info("Rest API called: getSnapshotNoDetail");

		return snapshotService.getLatestSnapshotNoDetail();
	}
}
