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

import com.apptivedeals.monitor.to.Gender;

@Component
public class GenderDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Map<Long, Gender> getGenders() {
		return jdbcTemplate.query(Queries.GET_ALL_GENDER, new ResultSetExtractor<Map<Long, Gender>>() {

			@Override
			public Map<Long, Gender> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<Long, Gender> genders = new HashMap<Long, Gender>();

				while (rs.next()) {
					Gender gender = new Gender();
					gender.setId(rs.getLong(1));
					gender.setName(rs.getString(2));
					gender.setCreateDate(rs.getTimestamp(3));
					gender.setUpdateDate(rs.getTimestamp(4));
					gender.setIsActive(rs.getBoolean(5));

					genders.put(gender.getId(), gender);
				}

				return genders;
			}

		});
	}
	
	public Map<Long, String> getGenderNames() {
		return jdbcTemplate.query(Queries.GET_ALL_GENDER, new ResultSetExtractor<Map<Long, String>>() {
			
			@Override
			public Map<Long, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<Long, String> genderNames = new HashMap<Long, String>();
				
				while (rs.next()) {
					genderNames.put(rs.getLong(1), rs.getString(2));
				}
				
				return genderNames;
			}
		});
	}
	
	public Long getGenderId(String genderName) {
		return jdbcTemplate.query(Queries.GET_GENDER_ID_BY_NAME, new Object[] { genderName },
				new ResultSetExtractor<Long>() {

					@Override
					public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
						Long genderId = null;

						if (rs.next()) {
							genderId = rs.getLong(1);
						}

						return genderId;
					}

				});
	}

	public Long insertGender(String genderName) {
		return jdbcTemplate.queryForObject(Queries.INSERT_GENDER, new Object[] { genderName }, Long.class);
	}
}
