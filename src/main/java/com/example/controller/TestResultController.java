package com.example.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.example.entity.JobApply;
import com.example.entity.JobSekers;

import jakarta.servlet.http.HttpSession;
@Controller
@RequestMapping("/jobseeker")
public class TestResultController {

	
	
	
	@GetMapping("/viewTestresult")
	public ModelAndView viewTestres(Model model, HttpSession session) {
		JobSekers jobsek = (JobSekers) session.getAttribute("jobseeker");

		int userId = jobsek.getUserId();
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Object[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/getResultsOfUser/" + userId, Object[].class);
		Object[] responseBody = responseEntity.getBody();
		List<Object> testResultList = Arrays.asList(responseBody);
		System.out.println("testResultList=" + testResultList);

		model.addAttribute("testResultList", testResultList);

		//////////////////////////////////////
		
		ModelAndView view = new ModelAndView("jobseeker/viewTestResult");
		return view;
	}
}

