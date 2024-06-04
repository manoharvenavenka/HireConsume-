package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data

public class TestResult {

	private int resultId;

	private JobSekers jobsekers;
	


	private Questions questions;
	
	

	private Vacancy vacancy;

	
	private String selectedOption;
	
	
	
	private String result;
	
	

	private String score;
	
	
	
}
