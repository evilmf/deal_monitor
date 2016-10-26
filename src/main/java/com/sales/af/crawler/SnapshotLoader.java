package com.sales.af.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sales.af.bo.Product;
import com.sales.af.bo.SnapshotDetail;
import com.sales.af.dao.SnapshotDetailDao;
import com.sales.af.to.SnapshotDetailTo;

public class SnapshotLoader {
	private static Logger LOGGER = Logger.getLogger(SnapshotLoader.class);
	
	@Autowired
	ProductQueueService productQueueService;
	
	@Autowired
	SnapshotDetailDao snapshotDetailDao;
	
	public void load() {
		Map<Long, SnapshotDetailTo> productMap = productQueueService.pollSnapshotQueue();
		
		if(productMap == null || productMap.isEmpty()) {
			return;
		}
		
		List<SnapshotDetail> snapshotDetailList = new ArrayList<SnapshotDetail>();
		Map<Long, SnapshotDetail> currentSnapshotMap = snapshotDetailDao.getCurrentSnapshot();
		
		for (Long pid : currentSnapshotMap.keySet()) {
			if (!productMap.containsKey(pid) || currentSnapshotMap.get(pid).getPriceDiscount() != productMap
					.get(pid).getPriceDiscount()) {
				SnapshotDetail snapshotDetail = new SnapshotDetail();
				snapshotDetail.setIsActive(false);
				snapshotDetail.setPriceDiscount(currentSnapshotMap.get(pid).getPriceDiscount());
				snapshotDetail.setPriceRegular(currentSnapshotMap.get(pid).getPriceRegular());
				snapshotDetail.setProduct(currentSnapshotMap.get(pid).getProduct());

				snapshotDetailList.add(snapshotDetail);
			}
		}

		for (Long pid : productMap.keySet()) {
			if (!currentSnapshotMap.containsKey(pid) || currentSnapshotMap.get(pid).getPriceDiscount() != productMap
					.get(pid).getPriceDiscount()) {
				SnapshotDetail snapshotDetail = new SnapshotDetail();
				snapshotDetail.setIsActive(true);
				snapshotDetail.setPriceDiscount(productMap.get(pid).getPriceDiscount());
				snapshotDetail.setPriceRegular(productMap.get(pid).getPriceRegular());
				Product product = new Product();
				product.setId(pid);
				snapshotDetail.setProduct(product);

				snapshotDetailList.add(snapshotDetail);
			}
		}

		LOGGER.info(String.format("Start Inserting %s SnapshotDetail.", snapshotDetailList.size()));

		Long currentSnapshotId = 0L;
		if (!snapshotDetailList.isEmpty()) {
			currentSnapshotId = snapshotDetailDao.insertSnapshotDetail(snapshotDetailList);
		}

		List<Long> sidList = snapshotDetailDao.getMissingSnapshotId(currentSnapshotId);

		for (Long sid : sidList) {
			Map<Long, SnapshotDetailTo> snapshotDetailMap = snapshotDetailDao.generateSnapshotDetailById(sid);

			LOGGER.debug(snapshotDetailDao.insertSnapshot(snapshotDetailMap, sid));
		}
		
		LOGGER.info(String.format("Done running snapshot loader! Snapshot change: %s.", snapshotDetailList.size()));
	}
}
