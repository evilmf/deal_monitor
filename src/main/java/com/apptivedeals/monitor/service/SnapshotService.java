package com.apptivedeals.monitor.service;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apptivedeals.monitor.dao.SnapshotDao;
import com.apptivedeals.monitor.to.Snapshot;
import com.apptivedeals.monitor.to.SnapshotDetail;

@Service
public class SnapshotService {
	
	@Autowired
	private SnapshotDao snapshotDao;
	
	public Snapshot getLatestSnapshotNoDetail() {
		return snapshotDao.getLatestSnapshotNoDetail();
	}
	
	public Snapshot getLatestSnapshot() {
		return snapshotDao.getLatestSnapshot();
	}
	
	public Snapshot getSnapshotById(long snapshotId) {
		return snapshotDao.getSnapshotById(snapshotId);
	}
	
	public Map<Long, SnapshotDetail> getCurrentSnapshot() {
		return snapshotDao.getCurrentSnapshot();
	}
}
