package com.sales.af.dao;

import com.sales.af.bo.Category;

public interface CategoryDao {
    public Category getCategoryByName(String categoryName);

    public Category insertCategory(Category category);
}
