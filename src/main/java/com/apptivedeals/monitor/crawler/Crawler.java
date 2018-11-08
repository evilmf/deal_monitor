package com.apptivedeals.monitor.crawler;

import java.util.Set;

import com.apptivedeals.monitor.to.Snapshot.SnapshotDetail;


public interface Crawler {
	public Set<SnapshotDetail> getProducts();
}