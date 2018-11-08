package com.apptivedeals.monitor.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.apptivedeals.monitor.to.Brand;

@Component
public class BrandDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Map<Long, Brand> getBrands() {
		return jdbcTemplate.query(Queries.GET_ALL_BRAND, new ResultSetExtractor<Map<Long, Brand>>() {

			@Override
			public Map<Long, Brand> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<Long, Brand> brands = new HashMap<Long, Brand>();

				while (rs.next()) {
					Brand brand = new Brand();
					brand.setId(rs.getLong(1));
					brand.setName(rs.getString(2));
					brand.setCreateDate(rs.getTimestamp(3));
					brand.setUpdateDate(rs.getTimestamp(4));
					brand.setIsActive(rs.getBoolean(5));

					brands.put(brand.getId(), brand);
				}

				return brands;
			}

		});
	}

	public Map<Long, String> getBrandNames() {
		return jdbcTemplate.query(Queries.GET_ALL_BRAND, new ResultSetExtractor<Map<Long, String>>() {

			@Override
			public Map<Long, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<Long, String> brandNames = new HashMap<Long, String>();

				while (rs.next()) {
					brandNames.put(rs.getLong(1), rs.getString(2));
				}

				return brandNames;
			}
		});
	}

	public Long getBrandId(String brandName) {
		return jdbcTemplate.query(Queries.GET_BRAND_ID_BY_NAME, new Object[] { brandName },
				new ResultSetExtractor<Long>() {

					@Override
					public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
						Long brandId = null;

						if (rs.next()) {
							brandId = rs.getLong(1);
						}

						return brandId;
					}

				});
	}

	public Long insertBrand(String brandName) {
		return jdbcTemplate.queryForObject(Queries.INSERT_BRAND, new Object[] { brandName }, Long.class);
	}
}
