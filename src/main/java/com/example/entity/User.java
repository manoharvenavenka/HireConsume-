package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
@Data
@Entity(name="user")
public class User {

	
	@Id
	private String username;
	
	private String password;
	
	private String role;
	
}
