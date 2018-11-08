package com.apptivedeals.monitor.to;

import java.util.List;

public class AfProducts {
	
	public List<Product> products;
	public Stats stats;
	
	@Override
	public String toString() {
		return "AfProducts [products=" + products
				+ ", stats=" + stats
				+ "]";
	}
	
	public static class Product {
		public Long productId;
		public String name;
		public String productUrl;
		public Float lowListPrice;
		public Float lowPrice;
		public String collection;
		public Boolean isSoldOut;
		
		@Override
		public String toString() {
			return "Product [productId=" + productId
					+ ", name=" + name
					+ ", productUrl=" + productUrl
					+ ", lowListPrice=" + lowListPrice
					+ ", lowPrice=" + lowPrice
					+ ", collection=" + collection
					+ ", isSoldOut=" + isSoldOut
					+ "]";
		}
	}
	
	public static class Stats {
		public Integer startNum;
		public Integer total;
		public Integer count;
		
		@Override
		public String toString() {
			return "Stats [startNum=" + startNum
					+ ", total=" + total
					+ ", count=" + count
					+ "]";
		}
	}
}
