package com.example.demo.dto;

public class FollowerGrowthDTO {
	
	 private String date;
	    private Long totalFollowers;

	    public FollowerGrowthDTO(String date, Long totalFollowers) {
	        this.date = date;
	        this.totalFollowers = totalFollowers;
	    }

	    public String getDate() { return date; }
	    public Long getTotalFollowers() { return totalFollowers; }

}
