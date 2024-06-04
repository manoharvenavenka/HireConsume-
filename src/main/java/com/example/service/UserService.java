package com.example.service;

import org.springframework.stereotype.Service;

import com.example.entity.User;

@Service
public interface UserService {
	User getUserByEmail(String username);
	boolean updateCompany(User user);
}
