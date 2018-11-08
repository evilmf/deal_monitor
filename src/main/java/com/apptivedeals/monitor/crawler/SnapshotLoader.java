package com.apptivedeals.monitor.crawler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.apptivedeals.monitor.dao.SnapshotDao;
import com.apptivedeals.monitor.to.Snapshot;
import com.apptivedeals.monitor.to.SnapshotDetail;

@Component
public class SnapshotLoader {
	private static Logger LOGGER = Logger.getLogger(SnapshotLoader.class);
	
	@Autowired
	private ProductQueueService productQueueService;
	
	@Autowired
	private SnapshotDao snapshotDao;
	
	@Scheduled(fixedDelay = 5000)
	public void load() {
		Map<Long, Snapshot.SnapshotDetail> productMap = productQueueService.pollSnapshotQueue();
		
		if(productMap == null || productMap.isEmpty()) {
			return;
		}
		
		LOGGER.info("Running snapshot loader...");
		
		List<SnapshotDetail> snapshotDetailList = new ArrayList<SnapshotDetail>();
		Map<Long, SnapshotDetail> currentSnapshotMap = snapshotDao.getCurrentSnapshot();
		
		for (Long pid : currentSnapshotMap.keySet()) {
			if (!productMap.containsKey(pid) || currentSnapshotMap.get(pid).getPriceDiscount() != productMap
					.get(pid).getPriceDiscount()) {
				SnapshotDetail snapshotDetail = new SnapshotDetail();
				snapshotDetail.setActive(false);
				snapshotDetail.setPriceDiscount(currentSnapshotMap.get(pid).getPriceDiscount());
				snapshotDetail.setPriceRegular(currentSnapshotMap.get(pid).getPriceRegular());
				snapshotDetail.setProductId(pid);
				snapshotDetail.setCreateDate(new Date());

				snapshotDetailList.add(snapshotDetail);
			}
		}

		for (Long pid : productMap.keySet()) {
			if (!currentSnapshotMap.containsKey(pid) || currentSnapshotMap.get(pid).getPriceDiscount() != productMap
					.get(pid).getPriceDiscount()) {
				SnapshotDetail snapshotDetail = new SnapshotDetail();
				snapshotDetail.setActive(true);
				snapshotDetail.setPriceDiscount(productMap.get(pid).getPriceDiscount());
				snapshotDetail.setPriceRegular(productMap.get(pid).getPriceRegular());
				snapshotDetail.setProductId(pid);
				snapshotDetail.setCreateDate(new Date());

				snapshotDetailList.add(snapshotDetail);
			}
		}

		LOGGER.info(String.format("Start Inserting %s SnapshotDetail.", snapshotDetailList.size()));

		if (!snapshotDetailList.isEmpty()) {
			snapshotDao.insertSnapshotDetail(snapshotDetailList);
		}

		List<Long> sidList = snapshotDao.getMissingSnapshotId();

		for (Long sid : sidList) {
			Snapshot snapshot = snapshotDao.generateSnapshot(sid);

			snapshotDao.insertSnapshot(snapshot);
		}
		
		LOGGER.info(String.format("Done running snapshot loader! Snapshot change: %s.", snapshotDetailList.size()));
	}
}

