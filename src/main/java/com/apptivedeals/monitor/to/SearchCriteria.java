package com.apptivedeals.monitor.to;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class SearchCriteria {
	
	@NotNull 
    @Size(min=2, max=100)
	@Pattern(regexp = ".*[a-zA-Z]{2,}.*")
	private String searchKeyword;
	
	private Float minPrice;
	private Float maxPrice;
		
	public String getSearchKeyword() {
		return searchKeyword;
	}
	public void setSearchKeyword(String searchKeyword) {
		this.searchKeyword = searchKeyword;
	}
	public Float getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(Float minPrice) {
		this.minPrice = minPrice;
	}
	public Float getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(Float maxPrice) {
		this.maxPrice = maxPrice;
	}
	
	public String toString() {
		return "SearchCriteria[searchKeyword=" + searchKeyword + ", " 
				+ "minPrice=" + minPrice + ", "
				+ "maxPrice=" + maxPrice + "]";
	}
}

