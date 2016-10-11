package com.sales.af.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sales.af.bo.Product;
import com.sales.af.bo.SnapshotDetail;
import com.sales.af.dao.SnapshotDetailDao;
import com.sales.af.to.SnapshotDetailTo;
import com.sales.af.to.SnapshotTo;

public class SnapshotLoader extends ProductQueue {
	private static Logger logger = Logger.getLogger(SnapshotLoader.class);
	private SnapshotTo allProducts;

	@Autowired
	SnapshotDetailDao snapshotDetailDao;

	public void load() throws InterruptedException, IOException {
		allProducts = snapshotQueue.poll();
		if (allProducts != null && allProducts.getSnapshotDetail() != null && allProducts.getSnapshotDetail().size() > 0) {
			long startTime = System.currentTimeMillis();
			logger.info(String.format("Start Loading Snapshot %s Products for Brand: %s; Queue Size: %s",
					allProducts.getSnapshotDetail().size(), allProducts.getBrandId(), snapshotQueue.size()));

			buildSnapshot();

			long endTime = System.currentTimeMillis();
			logger.info(String.format("Done Loading Snapshot for Brand %s; Queue Size: %s; Duration: %s ms",
					allProducts.getBrandId(), productQueue.size(), endTime - startTime));
		}
	}

	public void buildSnapshot() throws IOException {
		List<SnapshotDetail> snapshotDetailList = new ArrayList<SnapshotDetail>();
		Map<Long, SnapshotDetail> currentSnapshotMap = snapshotDetailDao
				.getCurrentSnapshotByBrandId(allProducts.getBrandId());
		Map<Long, SnapshotDetailTo> productMap = allProducts.getSnapshotDetail();

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

		logger.info(String.format("Start Inserting %s SnapshotDetail for Brand %s", snapshotDetailList.size(),
				allProducts.getBrandId()));

		Long currentSnapshotId = 0L;
		if (!snapshotDetailList.isEmpty()) {
			currentSnapshotId = snapshotDetailDao.insertSnapshotDetail(snapshotDetailList);
		}

		List<Long> sidList = snapshotDetailDao.getMissingSnapshotId(currentSnapshotId);

		for (Long sid : sidList) {
			Map<Long, SnapshotDetailTo> snapshotDetailMap = snapshotDetailDao.generateSnapshotDetailById(sid);

			logger.debug(snapshotDetailDao.insertSnapshot(snapshotDetailMap, sid));
		}
	}
}