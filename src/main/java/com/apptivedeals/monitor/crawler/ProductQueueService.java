package com.apptivedeals.monitor.crawler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Service;

import com.apptivedeals.monitor.to.Snapshot.SnapshotDetail;

@Service
public class ProductQueueService {
	private static ConcurrentLinkedQueue<Set<SnapshotDetail>> productQueue;
	private static ConcurrentLinkedQueue<Map<Long, SnapshotDetail>> snapshotQueue;
	
	public ProductQueueService() {
		productQueue = new ConcurrentLinkedQueue<Set<SnapshotDetail>>();
		snapshotQueue = new ConcurrentLinkedQueue<Map<Long, SnapshotDetail>>();
	}
	
	public Set<SnapshotDetail> pollProductQueue() {
		return productQueue.poll();
	}
	
	public void addProducts(Set<SnapshotDetail> products) {
		productQueue.add(products);
	}
	
	public Map<Long, SnapshotDetail> pollSnapshotQueue() {
		return snapshotQueue.poll();
	}
	
	public void addSnapshot(Map<Long, SnapshotDetail> snapshot) {
		snapshotQueue.add(snapshot);
	}
	
	public int productQueueSize() {
		return productQueue.size();
	}
}