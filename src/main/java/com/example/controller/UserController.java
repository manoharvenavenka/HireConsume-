package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import com.example.entity.User;
import com.example.service.UserService;

@Controller
public class UserController {
	@Autowired
	UserService userserv;

	@GetMapping("/userlogin")
	public ModelAndView loginForUser(Model model) {

		return new ModelAndView("/loginpage");
	}

	@GetMapping("/getUser/{username}")
	public ResponseEntity<Object> getCompany(@PathVariable("username") String username) {
		User use;

		use = userserv.getUserByEmail(username);

		ResponseEntity<Object> entity = new ResponseEntity<>(use, HttpStatus.OK);
		return entity;
	}

	@PutMapping(value = "/updateUser/{username}")
	public ResponseEntity<Object> updateUser(@PathVariable("username") String username, @RequestBody User user) {
		boolean flag;

		flag = userserv.updateCompany(user);
		System.err.println("haiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");

		System.out.println("failed");

		return new ResponseEntity<>(flag, HttpStatus.OK);
	}
}
