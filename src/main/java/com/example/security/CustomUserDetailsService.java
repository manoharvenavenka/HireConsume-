package com.example.security;

import java.util.Collection;
import java.util.Collections;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.entity.User;
import com.example.repository.UserRepository;





@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("IM HERE...");
		/*
		 * User user;
		 * 
		 * Optional<User> optionalUser = userRepository.findById(username);
		 * System.out.println("user===" + optionalUser.get());
		 * if(optionalUser.isEmpty()) { System.out.println("AAAA"); throw new
		 * UsernameNotFoundException("Invalid username or password11."); }else {
		 * System.out.println("BBBBB"); user=optionalUser.get(); }
		 * 
		 * CustomUserDetails customUserDetails=new CustomUserDetails(user); return
		 * customUserDetails;
		 */

		User user = userRepository.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not exists by Username or Email"));

		///////////
		System.out.println("user1=" + user);

		

	//////////////

	Collection<? extends GrantedAuthority> authorities=null;
	
	if(user.getRole().equals("ROLE_COMPANY")) {
		authorities = 
				Collections.singleton(new SimpleGrantedAuthority("ROLE_COMPANY"));
	}else if(user.getRole().equals("ROLE_ADMIN")) {
		authorities = 
				Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}else if(user.getRole().equals("ROLE_JOBSEEKER")) {
		authorities = 
				Collections.singleton(new SimpleGrantedAuthority("ROLE_JOBSEEKER"));
	}else {
		authorities = 
				Collections.singleton(new SimpleGrantedAuthority("ROLE_NONE"));
	}

	return new org.springframework.security.core.userdetails.User(username,user.getPassword(),authorities);

}

}
