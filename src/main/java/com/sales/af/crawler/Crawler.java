package com.sales.af.crawler;

import java.util.Set;

import com.sales.af.to.SnapshotDetailTo;

public interface Crawler {
	public Set<SnapshotDetailTo> getProducts();
}
