package com.sales.af.crawler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Service;

import com.sales.af.to.SnapshotDetailTo;

@Service
public class ProductQueueService {
	private static ConcurrentLinkedQueue<Set<SnapshotDetailTo>> productQueue;
	private static ConcurrentLinkedQueue<Map<Long, SnapshotDetailTo>> snapshotQueue;
	
	public ProductQueueService() {
		productQueue = new ConcurrentLinkedQueue<Set<SnapshotDetailTo>>();
		snapshotQueue = new ConcurrentLinkedQueue<Map<Long, SnapshotDetailTo>>();
	}
	
	public Set<SnapshotDetailTo> pollProductQueue() {
		return productQueue.poll();
	}
	
	public void addProducts(Set<SnapshotDetailTo> products) {
		productQueue.add(products);
	}
	
	public Map<Long, SnapshotDetailTo> pollSnapshotQueue() {
		return snapshotQueue.poll();
	}
	
	public void addSnapshot(Map<Long, SnapshotDetailTo> snapshot) {
		snapshotQueue.add(snapshot);
	}
	
	public int productQueueSize() {
		return productQueue.size();
	}
}