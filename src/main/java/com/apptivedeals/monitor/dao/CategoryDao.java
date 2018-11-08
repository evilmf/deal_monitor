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

import com.apptivedeals.monitor.to.Category;

@Component
public class CategoryDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Map<Long, Category> getCategories() {
		return jdbcTemplate.query(Queries.GET_ALL_CATEGORY, new ResultSetExtractor<Map<Long, Category>>() {

			@Override
			public Map<Long, Category> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<Long, Category> categories = new HashMap<Long, Category>();

				while (rs.next()) {
					Category category = new Category();
					category.setId(rs.getLong(1));
					category.setName(rs.getString(2));
					category.setCreateDate(rs.getTimestamp(3));
					category.setUpdateDate(rs.getTimestamp(4));
					category.setIsActive(rs.getBoolean(5));

					categories.put(category.getId(), category);
				}

				return categories;
			}

		});
	}

	public Map<Long, String> getCategoryNames() {
		return jdbcTemplate.query(Queries.GET_ALL_CATEGORY, new ResultSetExtractor<Map<Long, String>>() {

			@Override
			public Map<Long, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<Long, String> categoryNames = new HashMap<Long, String>();

				while (rs.next()) {
					categoryNames.put(rs.getLong(1), rs.getString(2));
				}

				return categoryNames;
			}
		});
	}

	public Long getCategoryId(String categoryName) {
		return jdbcTemplate.query(Queries.GET_CATEGORY_ID_BY_NAME, new Object[] { categoryName },
				new ResultSetExtractor<Long>() {

					@Override
					public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
						Long categoryId = null;

						if (rs.next()) {
							categoryId = rs.getLong(1);
						}

						return categoryId;
					}

				});
	}

	public Long insertCategory(String categoryName) {
		return jdbcTemplate.queryForObject(Queries.INSERT_CATEGORY, new Object[] { categoryName }, Long.class);
	}
}
