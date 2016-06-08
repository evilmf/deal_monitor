package com.sales.af.dao;

import java.util.List;

import com.sales.af.bo.Brand;

public interface BrandDao {
	public Brand getBrandByName(String brandName);

	public Brand insertBrand(Brand brand);
	
	public Brand insertBrand(String brandName);

	public List<Brand> getBrands();
}
