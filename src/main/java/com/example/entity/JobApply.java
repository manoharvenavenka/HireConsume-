package com.example.entity;

import lombok.Data;

@Data
public class JobApply {
	private int applyId;

	private JobSekers jobsekers;

	private Vacancy vacancy;

	private String applyDate;
	
	private String status;
	
	private String finalscore;


}
