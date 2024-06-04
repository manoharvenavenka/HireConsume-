package com.example.entity;

import java.util.Date;


import lombok.Data;
@Data
public class Vacancy {

private int vacancyId;


private Company company;

private Date postDate;

private String jobTitle;


private String description;

private String requirements;

private int noOfVacancies;

private String openDate;


private String closeDate;




}
