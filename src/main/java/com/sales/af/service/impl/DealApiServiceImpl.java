package com.sales.af.service.impl;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sales.af.dao.SnapshotDetailDao;
import com.sales.af.service.DealApiService;
import com.sales.af.to.SnapshotTo;

@Service
public class DealApiServiceImpl implements DealApiService {
    @Autowired
    SnapshotDetailDao snapshotDeailDao;

    public SnapshotTo getSnapshotDetailById(Long snapshotId) {
	SnapshotTo snapshotTo = null;

	try {
	    snapshotTo = snapshotDeailDao.getSnapshotDetailById(snapshotId);
	} catch (JsonParseException e) {
	    e.printStackTrace();
	} catch (JsonMappingException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return snapshotTo;
    }

    public SnapshotTo getLatestSnapshotDetail() {
	SnapshotTo snapshotTo = null;

	try {
	    snapshotTo = snapshotDeailDao.getLatestSnapshotDetail();
	} catch (JsonParseException e) {
	    e.printStackTrace();
	} catch (JsonMappingException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return snapshotTo;
    }

    public SnapshotTo getSnapshotNoDetail() {
	SnapshotTo snapshotTo = null;

	snapshotTo = snapshotDeailDao.getSnapshotNoDetail();

	return snapshotTo;
    }
}
