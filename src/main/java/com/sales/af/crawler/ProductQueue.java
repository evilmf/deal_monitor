package com.sales.af.crawler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.sales.af.to.ProductsTo;

public abstract class ProductQueue {
	public static ConcurrentLinkedQueue<ProductsTo> productQueue = new ConcurrentLinkedQueue<ProductsTo>();
	
	public static ConcurrentLinkedQueue<ProductsTo> snapshotQueue = new ConcurrentLinkedQueue<ProductsTo>();

	public static ConcurrentHashMap<String, Long> existingBrand = new ConcurrentHashMap<String, Long>();

	public static ConcurrentHashMap<String, Long> existingGender = new ConcurrentHashMap<String, Long>();

	public static ConcurrentHashMap<String, Long> existingCategory = new ConcurrentHashMap<String, Long>();
}
