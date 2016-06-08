package com.sales.af.crawler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.sales.af.to.SnapshotTo;

public abstract class ProductQueue {
	public static ConcurrentLinkedQueue<SnapshotTo> productQueue = new ConcurrentLinkedQueue<SnapshotTo>();

	public static ConcurrentLinkedQueue<SnapshotTo> snapshotQueue = new ConcurrentLinkedQueue<SnapshotTo>();

	public static ConcurrentHashMap<String, Long> existingBrand = new ConcurrentHashMap<String, Long>();

	public static ConcurrentHashMap<String, Long> existingGender = new ConcurrentHashMap<String, Long>();

	public static ConcurrentHashMap<String, Long> existingCategory = new ConcurrentHashMap<String, Long>();
}
