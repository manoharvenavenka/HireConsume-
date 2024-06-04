package com.example.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.Company;
import com.example.entity.JobApply;
import com.example.entity.JobSekers;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/jobseeker")

public class JobApplayController {

	@GetMapping("/addJobApplay/{vacancyId}")
	public ModelAndView addJobApplay(@PathVariable("vacancyId") int vacancyId, Model model, HttpSession session) {

		JobSekers job1 = (JobSekers) session.getAttribute("jobseeker");
		int userId = job1.getUserId();

		JobApply jobapplay = new JobApply();
		// jobapplay.getVacancy().setVacancyId(vacancyId);
		model.addAttribute("vacancyId", vacancyId);
		model.addAttribute("userId", userId);
		model.addAttribute("jobapplay", jobapplay);
		ModelAndView view = new ModelAndView("jobseeker/addJobApplay");
		return view;
	}

	@PostMapping("/addingJobApplay")
	public String postMethodName(@ModelAttribute("jobapplay") JobApply jobapplay, Model model) {

		String status = "OnProgress";

		jobapplay.setStatus(status);
		
		jobapplay.setFinalscore("0");

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		HttpEntity<JobApply> entity = new HttpEntity<JobApply>(jobapplay, headers);

		RestTemplate restTemplate = new RestTemplate();

		String url = "http://3.108.237.172:8080/HireMeNow/api/v1/addJobApplay";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		String responseString = response.getBody();
		System.out.println("responseString" + responseString);

		jobapplay = new JobApply();

		model.addAttribute("jobapplay", jobapplay);

		model.addAttribute("responseString", responseString);
		model.addAttribute("RESP", "*ThankYou for Applaying");
		return "redirect:/jobseeker/jobHome?message="+model.getAttribute("RESP");
	} 

	@GetMapping("/viewApply")
	public ModelAndView viewVecncies(Model model, HttpSession session) {
		JobSekers jobsek = (JobSekers) session.getAttribute("jobseeker");

		int userId = jobsek.getUserId();
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<JobApply[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/viewJobapplay/" + userId, JobApply[].class);
		JobApply[] responseBody = responseEntity.getBody();
		List<JobApply> jobapplyList = Arrays.asList(responseBody);
		System.out.println("jobapplyList=" + jobapplyList);

		model.addAttribute("jobapplyList", jobapplyList);

		//////////////////////////////////////
		
		ModelAndView view = new ModelAndView("jobseeker/viewJobapply");
		return view;
	}
}
