package com.apptivedeals.monitor.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.apptivedeals.monitor.to.Product;
import com.apptivedeals.monitor.to.ProductSearchResult;
import com.apptivedeals.monitor.to.ProductSnapshots;
import com.apptivedeals.monitor.to.SearchCriteria;

@Component
public class ProductDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<ProductSearchResult> searchProduct(SearchCriteria searchCriteria) {
		String queryString = Queries.PRODUCT_SEARCH;
		queryString += searchCriteria.getMinPrice() == null ? ""
				: " and sd.price_discount >= " + searchCriteria.getMinPrice() + " ";
		queryString += searchCriteria.getMaxPrice() == null ? ""
				: " and sd.price_discount <= " + searchCriteria.getMaxPrice() + " ";
		queryString += " order by ts_rank desc";

		return jdbcTemplate.query(queryString,
				new Object[] { searchCriteria.getSearchKeyword(), searchCriteria.getSearchKeyword() },
				new RowMapper<ProductSearchResult>() {

					@Override
					public ProductSearchResult mapRow(ResultSet rs, int num) throws SQLException {
						ProductSearchResult productSearchResult = new ProductSearchResult();
						productSearchResult.setProductId(rs.getLong(1));
						productSearchResult.setProductName(rs.getString(2));
						productSearchResult.setBrandName(rs.getString(3));
						productSearchResult.setGenderName(rs.getString(4));
						productSearchResult.setCategoryName(rs.getString(5));
						productSearchResult.setImageUrl(rs.getString(6));
						productSearchResult.setMinPrice(rs.getFloat(7));
						productSearchResult.setMaxPrice(rs.getFloat(8));
						productSearchResult.setSnapshotCount(rs.getInt(9));
						productSearchResult.setScore(rs.getFloat(10));

						return productSearchResult;
					}

				});
	}

	public ProductSnapshots getProductSnapshots(Long productId) {
		return jdbcTemplate.query(Queries.GET_SNAPSHOT_LIST_BY_PRODUCT_ID, new Object[] { productId, productId },
				new ResultSetExtractor<ProductSnapshots>() {

					@Override
					public ProductSnapshots extractData(ResultSet rs) throws SQLException, DataAccessException {
						ProductSnapshots productSnapshots = null;
						if (rs.next()) {
							productSnapshots = new ProductSnapshots();
							productSnapshots.setProductName(rs.getString(3));
							productSnapshots.setBrandId(rs.getLong(4));
							productSnapshots.setBrand(rs.getString(5));
							productSnapshots.setGenderId(rs.getLong(6));
							productSnapshots.setGender(rs.getString(7));
							productSnapshots.setCategoryId(rs.getLong(8));
							productSnapshots.setCategory(rs.getString(9));

							List<ProductSnapshots.ProductSnapshot> productSnapshotList = new LinkedList<ProductSnapshots.ProductSnapshot>();
							do {
								ProductSnapshots.ProductSnapshot productSnapshot = new ProductSnapshots.ProductSnapshot();
								productSnapshot.setActiveDate(rs.getTimestamp(11));
								productSnapshot.setDuration(rs.getFloat(13));
								productSnapshot.setInactiveDate(rs.getTimestamp(12));
								productSnapshot.setPrice(rs.getFloat(10));
								productSnapshot.setProductId(rs.getLong(2));
								productSnapshot.setSnapshotId(rs.getLong(1));

								productSnapshotList.add(productSnapshot);
							} while (rs.next());

							productSnapshots.setProductSnapshotList(productSnapshotList);
						}

						return productSnapshots;
					}

				});
	}

	public Map<String, Product> getProductByBrandId(Long brandId) {
		return jdbcTemplate.query(Queries.GET_PRODUCTS_BY_BRAND_ID, new Object[] { brandId },
				new ResultSetExtractor<Map<String, Product>>() {

					@Override
					public Map<String, Product> extractData(ResultSet rs) throws SQLException, DataAccessException {
						Map<String, Product> products = new HashMap<String, Product>();
						while (rs.next()) {
							Product product = new Product();
							product.setId(rs.getLong(1));
							product.setProductId(rs.getString(2));
							product.setCategoryId(rs.getLong(3));
							product.setBrandId(rs.getLong(4));
							product.setGenderId(rs.getLong(5));
							product.setName(rs.getString(6));
							product.setUrl(rs.getString(7));
							product.setCreateDate(rs.getTimestamp(8));
							product.setUpdateDate(rs.getTimestamp(9));
							product.setActive(rs.getBoolean(10));

							products.put(rs.getString(2), product);
						}

						return products;
					}

				});
	}

	public long insertProduct(Product product) {
		return jdbcTemplate.queryForObject(Queries.INSERT_PRODUCT,
				new Object[] { product.getProductId(), product.getCategoryId(), product.getBrandId(),
						product.getGenderId(), product.getName(), product.getUrl(),
						new java.sql.Timestamp(product.getCreateDate().getTime()),
						new java.sql.Timestamp(product.getUpdateDate().getTime()), product.isActive() },
				Long.class);
	}

	public void updateProductUrl(Long productId, String productUrl) {
		jdbcTemplate.update(Queries.UPDATE_PRODUCT_URL_BY_ID, new Object[] { productUrl, productId });
	}
}
