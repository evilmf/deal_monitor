package com.sales.af.dao;

import java.util.List;

import com.sales.af.bo.Gender;

public interface GenderDao {
	public Gender getGenderByName(String genderName);

	public Gender insertGender(Gender gender);
	
	public List<Gender> getGenders();
}
