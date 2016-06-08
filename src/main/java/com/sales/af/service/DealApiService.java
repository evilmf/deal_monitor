package com.sales.af.service;

import com.sales.af.to.ClassificationTo;
import com.sales.af.to.SnapshotTo;

public interface DealApiService {
	SnapshotTo getSnapshotDetailById(Long snapshotId);

	public SnapshotTo getLatestSnapshotDetail();

	public SnapshotTo getSnapshotNoDetail();

	public ClassificationTo getClassification();
}
