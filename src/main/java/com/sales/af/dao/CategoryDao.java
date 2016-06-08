package com.sales.af.dao;

import java.util.List;

import com.sales.af.bo.Category;

public interface CategoryDao {
	public Category getCategoryByName(String categoryName);

	public Category insertCategory(Category category);
	
	public Category insertCategory(String categoryName);

	public List<Category> getCategories();
}
