package com.sales.af.dao;

import com.sales.af.bo.Brand;

public interface BrandDao {
    public Brand getBrandByName(String brandName);

    public Brand insertBrand(Brand brand);
}
