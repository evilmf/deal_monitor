package com.apptivedeals.monitor.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.apptivedeals.monitor.to.Image;

@Component
public class ImageDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void insertImage(Image image) {
		jdbcTemplate.update(Queries.INSERT_IMAGE, new Object[] { image.getUrl(), image.getProductId(),
				image.getCreateDate(), image.getUpdateDate(), image.isActive() });
	}
}
