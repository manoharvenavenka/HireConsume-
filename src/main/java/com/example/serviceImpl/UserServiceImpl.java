package com.example.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.User;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.UserRepository;
import com.example.service.UserService;


@Service
public class UserServiceImpl implements UserService{
	
@Autowired
UserRepository userrepo;

@Override
public User getUserByEmail(String username) {
	Optional<User> company = userrepo.findById(username);
	User comp;
	if(company.isPresent()) {
		comp=company.get();
	}else {
		throw new ResourceNotFoundException("User","email", username);
	}
	return comp;
		}

@Override
public boolean updateCompany(User user) {
	Optional<User> company1 = userrepo.findById(user.getUsername());
	
	if(company1.isPresent()) {
		userrepo.save(user);
		return true;
		
		
	}else {
		return false;
	}


}
}
