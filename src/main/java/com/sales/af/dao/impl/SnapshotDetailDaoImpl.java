package com.sales.af.dao.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sales.af.bo.Product;
import com.sales.af.bo.Snapshot;
import com.sales.af.bo.SnapshotDetail;
import com.sales.af.dao.SnapshotDetailDao;
import com.sales.af.dao.UtilDao;
import com.sales.af.to.SnapshotDetailTo;
import com.sales.af.to.SnapshotTo;

@Repository
public class SnapshotDetailDaoImpl implements SnapshotDetailDao {
	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	UtilDao utilDao;

	public Map<Long, SnapshotDetail> getCurrentSnapshotByBrandId(long brandId) {
		Map<Long, SnapshotDetail> snapshot = new HashMap<Long, SnapshotDetail>();

		Query query = entityManager.createNamedQuery("getCurrentSnapshotByBrand");
		query.setParameter("brandId", brandId);

		@SuppressWarnings("unchecked")
		List<Object> products = query.getResultList();
		for (Object p : products) {
			Object[] data = (Object[]) p;
			SnapshotDetail sd = new SnapshotDetail();
			sd.setId(((BigInteger) data[0]).longValue());
			Product product = new Product();
			product.setId(((BigInteger) data[1]).longValue());
			sd.setProduct(product);
			sd.setPriceRegular(((BigDecimal) data[2]).floatValue());
			sd.setPriceDiscount(((BigDecimal) data[3]).floatValue());
			sd.setSnapshotId(((BigInteger) data[4]).longValue());
			sd.setIsActive((Boolean) data[5]);
			snapshot.put(sd.getProduct().getId(), sd);
		}

		return snapshot;
	}
	
	public Map<Long, SnapshotDetail> getCurrentSnapshot() {
		Map<Long, SnapshotDetail> snapshot = new HashMap<Long, SnapshotDetail>();

		Query query = entityManager.createNamedQuery("getCurrentSnapshot");

		@SuppressWarnings("unchecked")
		List<Object> products = query.getResultList();
		for (Object p : products) {
			Object[] data = (Object[]) p;
			SnapshotDetail sd = new SnapshotDetail();
			sd.setId(((BigInteger) data[0]).longValue());
			Product product = new Product();
			product.setId(((BigInteger) data[1]).longValue());
			sd.setProduct(product);
			sd.setPriceRegular(((BigDecimal) data[2]).floatValue());
			sd.setPriceDiscount(((BigDecimal) data[3]).floatValue());
			sd.setSnapshotId(((BigInteger) data[4]).longValue());
			sd.setIsActive((Boolean) data[5]);
			snapshot.put(sd.getProduct().getId(), sd);
		}
		
		return snapshot;
	}

	@Transactional
	public Long insertSnapshotDetail(List<SnapshotDetail> snapshotDetailList) {
		Query query = entityManager.createNamedQuery("getNextSnapshotId");
		Long currentSnapshotId = ((BigInteger) query.getSingleResult()).longValue();

		for (SnapshotDetail sd : snapshotDetailList) {
			sd.setSnapshotId(currentSnapshotId);
			entityManager.merge(sd);
		}

		return currentSnapshotId;
	}

	@SuppressWarnings("unchecked")
	public List<Long> getMissingSnapshotId(Long currentSnapshotId) {
		Query query = entityManager.createNamedQuery("getMissingSnapshotId");
		query.setParameter("sid", currentSnapshotId);

		List<BigInteger> res = query.getResultList();
		List<Long> sidList = new ArrayList<Long>(res.size());
		for (BigInteger sid : res) {
			sidList.add(sid.longValue());
		}

		return sidList;
	}

	public Map<Long, SnapshotDetailTo> generateSnapshotDetailById(Long id) {
		Map<Long, SnapshotDetailTo> snapshotDetailList = new HashMap<Long, SnapshotDetailTo>();
		Query query = entityManager.createNamedQuery("getSnapshotBySid");
		query.setParameter("sid", id);

		@SuppressWarnings("unchecked")
		List<Object> res = query.getResultList();
		for (Object row : res) {
			Object[] data = (Object[]) row;

			Long productId = ((BigInteger) data[1]).longValue();
			if (!snapshotDetailList.containsKey(productId)) {
				SnapshotDetailTo sdt = new SnapshotDetailTo();
				List<String> images = new ArrayList<String>();

				sdt.setSnapshotDetailId(((BigInteger) data[0]).longValue());
				sdt.setProductId(productId);
				sdt.setProductDataId((String) data[2]);
				sdt.setProductCreateDate(new Date(((Timestamp) data[3]).getTime()));
				sdt.setBrandId(((BigInteger) data[4]).longValue());
				sdt.setBrandName((String) data[5]);
				sdt.setGenderId(((BigInteger) data[6]).longValue());
				sdt.setGenderName((String) data[7]);
				sdt.setCategoryId(((BigInteger) data[8]).longValue());
				sdt.setCategoryName((String) data[9]);
				sdt.setSnapshotId(((BigInteger) data[10]).longValue());
				sdt.setPriceRegular(((BigDecimal) data[11]).floatValue());
				sdt.setPriceDiscount(((BigDecimal) data[12]).floatValue());
				sdt.setDiscount(((BigDecimal) data[13]).floatValue());
				sdt.setIsNew((Boolean) data[14]);
				sdt.setSnapshotCreateDate(new Date(((Timestamp) data[15]).getTime()));
				sdt.setImages(images);
				sdt.setProductUrl((String) data[16]);
				sdt.setProductName((String) data[17]);

				snapshotDetailList.put(productId, sdt);
			}

			snapshotDetailList.get(productId).addImage((String) data[18]);
		}

		return snapshotDetailList;
	}

	@Transactional
	public String insertSnapshot(Map<Long, SnapshotDetailTo> snapshot, Long sid) {
		String snapshotJson = "";
		try {
			snapshotJson = objectMapper.writeValueAsString(snapshot);
			
			Snapshot sd = new Snapshot();
			sd.setSnapshot(snapshotJson);
			sd.setId(sid);
			entityManager.merge(sd);
			
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return snapshotJson;
	}

	public SnapshotTo getSnapshotDetailById(Long currentSnapshotId)
			throws JsonParseException, JsonMappingException, IOException {
		Snapshot snapshot = utilDao.get(currentSnapshotId, Snapshot.class);
		SnapshotTo snapshotTo = new SnapshotTo();
		if (snapshot == null) {
			return snapshotTo;
		}
		snapshotTo = new SnapshotTo(snapshot);

		TypeReference<HashMap<Long, SnapshotDetailTo>> typeRef = new TypeReference<HashMap<Long, SnapshotDetailTo>>() {
		};

		Map<Long, SnapshotDetailTo> products = objectMapper.readValue(snapshot.getSnapshot(), typeRef);
		snapshotTo.setSnapshotDetail(products);

		return snapshotTo;
	}

	public SnapshotTo getLatestSnapshotDetail() throws JsonParseException, JsonMappingException, IOException {
		Query query = entityManager.createNamedQuery("getLatestSnapshotDetail");
		query.setMaxResults(1);

		Snapshot snapshot = (Snapshot) query.getSingleResult();
		SnapshotTo snapshotTo = new SnapshotTo(snapshot);

		TypeReference<HashMap<Long, SnapshotDetailTo>> typeRef = new TypeReference<HashMap<Long, SnapshotDetailTo>>() {
		};

		Map<Long, SnapshotDetailTo> products = objectMapper.readValue(snapshot.getSnapshot(), typeRef);
		snapshotTo.setSnapshotDetail(products);

		return snapshotTo;
	}

	public SnapshotTo getSnapshotNoDetail() {
		Query query = entityManager.createNamedQuery("getLatestSnapshotDetail");
		query.setMaxResults(1);

		Snapshot snapshot = (Snapshot) query.getSingleResult();
		SnapshotTo snapshotTo = new SnapshotTo(snapshot);

		return snapshotTo;
	}
	
	public Object[] getSnapshotListByProductId(Long productId) {
		Query query = entityManager.createNamedQuery("getSnapshotListByProductId");
		query.setParameter("product_id", productId);
		
		return query.getResultList().toArray();
	}
}
