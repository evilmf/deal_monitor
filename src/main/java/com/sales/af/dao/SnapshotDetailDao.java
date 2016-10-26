package com.sales.af.dao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;

import com.sales.af.bo.SnapshotDetail;
import com.sales.af.to.SnapshotDetailTo;
import com.sales.af.to.SnapshotTo;

public interface SnapshotDetailDao {
	public Map<Long, SnapshotDetail> getCurrentSnapshotByBrandId(long brandId);

	public Long insertSnapshotDetail(List<SnapshotDetail> snapshotDetailList);

	public List<Long> getMissingSnapshotId(Long currentSnapshotId);

	public Map<Long, SnapshotDetailTo> generateSnapshotDetailById(Long currentSnapshotId);

	public String insertSnapshot(Map<Long, SnapshotDetailTo> snapshot, Long sid);

	public SnapshotTo getSnapshotDetailById(Long currentSnapshotId)
			throws JsonParseException, JsonMappingException, IOException;

	public SnapshotTo getLatestSnapshotDetail() throws JsonParseException, JsonMappingException, IOException;

	public SnapshotTo getSnapshotNoDetail();
	
	public Object[] getSnapshotListByProductId(Long productId);
	
	public Map<Long, SnapshotDetail> getCurrentSnapshot();
}
