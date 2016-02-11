package com.sales.af.service.impl;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sales.af.bo.Brand;
import com.sales.af.bo.Category;
import com.sales.af.bo.Gender;
import com.sales.af.dao.BrandDao;
import com.sales.af.dao.CategoryDao;
import com.sales.af.dao.GenderDao;
import com.sales.af.dao.SnapshotDetailDao;
import com.sales.af.service.DealApiService;
import com.sales.af.to.ClassificationTo;
import com.sales.af.to.SnapshotTo;

@Service
public class DealApiServiceImpl implements DealApiService {
    @Autowired
    SnapshotDetailDao snapshotDeailDao;
    
    @Autowired
    BrandDao brandDao;
    
    @Autowired
    CategoryDao categoryDao;
    
    @Autowired
    GenderDao genderDao;
    
    public SnapshotTo getSnapshotDetailById(Long snapshotId) {
	SnapshotTo snapshotTo = null;

	try {
	    snapshotTo = snapshotDeailDao.getSnapshotDetailById(snapshotId);
	} catch (JsonParseException e) {
	    e.printStackTrace();
	} catch (JsonMappingException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return snapshotTo;
    }

    public SnapshotTo getLatestSnapshotDetail() {
	SnapshotTo snapshotTo = null;

	try {
	    snapshotTo = snapshotDeailDao.getLatestSnapshotDetail();
	} catch (JsonParseException e) {
	    e.printStackTrace();
	} catch (JsonMappingException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return snapshotTo;
    }

    public SnapshotTo getSnapshotNoDetail() {
		SnapshotTo snapshotTo = null;
	
		snapshotTo = snapshotDeailDao.getSnapshotNoDetail();
	
		return snapshotTo;
    }
    
    public ClassificationTo getClassification() {
    	ClassificationTo classification = new ClassificationTo();
    	
    	List<Brand> brands = brandDao.getBrands();
    	for(Brand brand : brands) {
    		classification.getBrands().put(brand.getId(), brand.getName());
    	}
    	
    	List<Gender> genders = genderDao.getGenders();
    	for(Gender gender : genders) {
    		classification.getGenders().put(gender.getId(), gender.getName());
    	}
    	
    	List<Category> categories = categoryDao.getCategories();
    	for(Category category : categories) {
    		classification.getCategories().put(category.getId(), category.getName());
    	}
    	
    	return classification;
    }
}
