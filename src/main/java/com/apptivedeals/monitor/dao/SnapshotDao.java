package com.apptivedeals.monitor.dao;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.apptivedeals.monitor.to.Snapshot;
import com.apptivedeals.monitor.to.SnapshotDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SnapshotDao {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static Logger LOGGER = Logger.getLogger(SnapshotDao.class);

	public Snapshot getLatestSnapshotNoDetail() {
		return jdbcTemplate.queryForObject(Queries.GET_LATEST_SNAPSHOT_NO_DETAIL, new RowMapper<Snapshot>() {

			@Override
			public Snapshot mapRow(ResultSet rs, int rowNum) throws SQLException {
				Snapshot snapshotTo = null;

				snapshotTo = new Snapshot();
				snapshotTo.setSnapshotId(rs.getLong(1));
				snapshotTo.setCreateDate(rs.getTimestamp(2));
				snapshotTo.setUpdateDate(rs.getTimestamp(3));
				snapshotTo.setActive(rs.getBoolean(4));

				return snapshotTo;
			}

		});
	}

	public Snapshot getLatestSnapshot() {
		return jdbcTemplate.queryForObject(Queries.GET_LATEST_SNAPSHOT, new RowMapper<Snapshot>() {

			@Override
			public Snapshot mapRow(ResultSet rs, int rowNum) throws SQLException {
				Snapshot snapshotTo = null;

				try {
					snapshotTo = new Snapshot();
					snapshotTo.setSnapshotId(rs.getLong(1));
					snapshotTo.setCreateDate(rs.getTimestamp(2));
					snapshotTo.setUpdateDate(rs.getTimestamp(3));
					snapshotTo.setActive(rs.getBoolean(4));

					TypeReference<HashMap<Long, Snapshot.SnapshotDetail>> typeRef = new TypeReference<HashMap<Long, Snapshot.SnapshotDetail>>() {
					};

					Map<Long, Snapshot.SnapshotDetail> snapshotDetail;
					snapshotDetail = objectMapper.readValue(rs.getString(5), typeRef);

					snapshotTo.setSnapshotDetail(snapshotDetail);
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}

				return snapshotTo;
			}
		});
	}

	public Snapshot getSnapshotById(long snapshotId) {
		return jdbcTemplate.query(Queries.GET_SNAPSHOT_BY_ID, new ResultSetExtractor<Snapshot>() {

			@Override
			public Snapshot extractData(ResultSet rs) throws SQLException, DataAccessException {
				Snapshot snapshotTo = null;

				if (rs.next()) {
					try {
						snapshotTo = new Snapshot();
						snapshotTo.setSnapshotId(rs.getLong(1));
						snapshotTo.setCreateDate(rs.getTimestamp(2));
						snapshotTo.setUpdateDate(rs.getTimestamp(3));
						snapshotTo.setActive(rs.getBoolean(4));

						TypeReference<HashMap<Long, Snapshot.SnapshotDetail>> typeRef = new TypeReference<HashMap<Long, Snapshot.SnapshotDetail>>() {
						};

						Map<Long, Snapshot.SnapshotDetail> snapshotDetail;
						snapshotDetail = objectMapper.readValue(rs.getString(5), typeRef);

						snapshotTo.setSnapshotDetail(snapshotDetail);
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}

				return snapshotTo;
			}
		}, snapshotId);
	}

	public Map<Long, SnapshotDetail> getCurrentSnapshot() {
		return jdbcTemplate.query(Queries.GET_CURRENT_SNAPSHOT, new ResultSetExtractor<Map<Long, SnapshotDetail>>() {

			@Override
			public Map<Long, SnapshotDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<Long, SnapshotDetail> snapshot = new HashMap<Long, SnapshotDetail>();
				while (rs.next()) {
					SnapshotDetail snapshotDetail = new SnapshotDetail();
					snapshotDetail.setId(rs.getLong(1));
					snapshotDetail.setProductId(rs.getLong(2));
					snapshotDetail.setPriceRegular(rs.getFloat(3));
					snapshotDetail.setPriceDiscount(rs.getFloat(4));
					snapshotDetail.setSnapshotId(rs.getLong(5));
					snapshotDetail.setActive(rs.getBoolean(6));
					snapshotDetail.setCreateDate(rs.getTimestamp(7));

					snapshot.put(snapshotDetail.getProductId(), snapshotDetail);
				}

				return snapshot;
			}

		});
	}

	public long insertSnapshotDetail(List<SnapshotDetail> snapshotDetailList) {
		long snapshotId = jdbcTemplate.queryForObject(Queries.GET_NEXT_SNAPSHOT_ID, Long.class);

		jdbcTemplate.batchUpdate(Queries.INSERT_SNAPSHOT_DETAIL, new BatchPreparedStatementSetter() {

			@Override
			public int getBatchSize() {
				return snapshotDetailList.size();
			}

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setLong(1, snapshotDetailList.get(i).getProductId());
				ps.setFloat(2, snapshotDetailList.get(i).getPriceRegular());
				ps.setFloat(3, snapshotDetailList.get(i).getPriceDiscount());
				ps.setLong(4, snapshotId);
				ps.setBoolean(5, snapshotDetailList.get(i).isActive());
				ps.setTimestamp(6, new java.sql.Timestamp(snapshotDetailList.get(i).getCreateDate().getTime()));
			}

		});

		return snapshotId;
	}

	public List<Long> getMissingSnapshotId() {
		return jdbcTemplate.queryForList(Queries.GET_MISSING_SNAPSHOT_ID, Long.class);
	}

	public Snapshot generateSnapshot(long snapshotId) {
		return jdbcTemplate.query(Queries.GENERATE_SNAPSHOT_BY_ID, new Object[] { snapshotId, snapshotId },
				new ResultSetExtractor<Snapshot>() {

					@Override
					public Snapshot extractData(ResultSet rs) throws SQLException, DataAccessException {
						Snapshot snapshot = new Snapshot();
						Map<Long, Snapshot.SnapshotDetail> snapshotDetailMap = new HashMap<Long, Snapshot.SnapshotDetail>();

						while (rs.next()) {
							snapshot.setSnapshotId(snapshotId);

							Snapshot.SnapshotDetail snapshotDetail = new Snapshot.SnapshotDetail();
							snapshotDetail.setSnapshotDetailId(rs.getLong(1));
							snapshotDetail.setProductId(rs.getLong(2));
							snapshotDetail.setProductDataId(rs.getString(3));
							snapshotDetail.setProductCreateDate(rs.getTimestamp(4));
							snapshotDetail.setBrandId(rs.getLong(5));
							snapshotDetail.setBrandName(rs.getString(6));
							snapshotDetail.setGenderId(rs.getLong(7));
							snapshotDetail.setGenderName(rs.getString(8));
							snapshotDetail.setCategoryId(rs.getLong(9));
							snapshotDetail.setCategoryName(rs.getString(10));
							snapshotDetail.setSnapshotId(rs.getLong(11));
							snapshotDetail.setPriceRegular(rs.getFloat(12));
							snapshotDetail.setPriceDiscount(rs.getFloat(13));
							snapshotDetail.setDiscount(rs.getFloat(14));
							snapshotDetail.setIsNew(rs.getBoolean(15));
							snapshotDetail.setSnapshotCreateDate(rs.getTimestamp(16));
							snapshotDetail.setProductUrl(rs.getString(17));
							snapshotDetail.setProductName(rs.getString(18));
							snapshotDetail.setImages(Arrays.asList(rs.getString(19)));

							snapshotDetailMap.put(rs.getLong(2), snapshotDetail);
						}

						snapshot.setActive(true);
						snapshot.setCreateDate(new java.util.Date());
						snapshot.setUpdateDate(new java.util.Date());
						snapshot.setSnapshotDetail(snapshotDetailMap);

						return snapshot;
					}

				});
	}

	public void insertSnapshot(Snapshot snapshot) {
		try {
			jdbcTemplate.update(Queries.INSERT_SNAPSHOT,
					new Object[] { snapshot.getSnapshotId(), new java.sql.Timestamp(snapshot.getCreateDate().getTime()),
							new java.sql.Timestamp(snapshot.getUpdateDate().getTime()), snapshot.getActive(),
							objectMapper.writeValueAsString(snapshot.getSnapshotDetail()) });
		} catch (DataAccessException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (JsonProcessingException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
