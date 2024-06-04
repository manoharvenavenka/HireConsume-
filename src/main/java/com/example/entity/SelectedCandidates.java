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

public class SelectedCandidates {


	private int id;


	private Vacancy vacancy;

	
	private Test test;

	private JobSekers jobSekers;


	private int score;


	private String status;

	
}
