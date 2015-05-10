package com.sales.af.dao;

import com.sales.af.bo.Gender;

public interface GenderDao {
	public Gender getGenderByName(String genderName);

	public Gender insertGender(Gender gender);
}
