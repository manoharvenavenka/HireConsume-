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

import com.example.entity.Company;
import com.example.entity.JobApply;
import com.example.entity.JobSekers;
import com.example.entity.SelectedCandidates;
import com.example.entity.Test;
import com.example.entity.TestResult;
import com.example.entity.Vacancy;

@Controller
@RequestMapping("/admin")
public class AdminController {
	RestTemplate restTemplate = new RestTemplate();
	@GetMapping("/adminHome") // Company home
	public ModelAndView adminHome(Model model) {
		
		ModelAndView view = new ModelAndView("admin/adminHome");

		return view;

	}
	@GetMapping("/AllCompanies") // Company home
	public ModelAndView companies(Model model) {
		ResponseEntity<Company[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/allCompanies", Company[].class);
		Company[] responseBody = responseEntity.getBody();
		List<Company> companyList = Arrays.asList(responseBody);

		model.addAttribute("companyList", companyList);
		System.out.println("companyList="+companyList);
		ModelAndView view = new ModelAndView("admin/viewCompanies");

		return view;

	}
	@GetMapping("/AllVacancies") // Company home
	public ModelAndView allVacancies(Model model) {

		ResponseEntity<Vacancy[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/allVacancies", Vacancy[].class);
		Vacancy[] responseBody = responseEntity.getBody();
		List<Vacancy> vacancylist = Arrays.asList(responseBody);

		model.addAttribute("vacancylist", vacancylist);
		System.out.println("vacancylist="+vacancylist);
		ModelAndView view = new ModelAndView("admin/viewAllVacancies");

		return view;

	}
	@GetMapping("/AllJobseekers") // Company home
	public ModelAndView allJobseekers(Model model) {

		ResponseEntity<JobSekers[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/allJobsekers", JobSekers[].class);
		JobSekers[] responseBody = responseEntity.getBody();
		List<JobSekers> jobseekerList = Arrays.asList(responseBody);
		System.out.println("jobseekerList=" + jobseekerList);

		model.addAttribute("jobseekerList", jobseekerList);
		ModelAndView view = new ModelAndView("admin/viewAllJobseekers");

		return view;

	}
	@GetMapping("/AllJobApplay") // Company home
	public ModelAndView allJobApplay(Model model) {

		ResponseEntity<JobApply[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/applies" , JobApply[].class);
		JobApply[] responseBody = responseEntity.getBody();
		List<JobApply> jobapplyList = Arrays.asList(responseBody);
		System.out.println("jobapplyList=" + jobapplyList);

		model.addAttribute("jobapplyList", jobapplyList);
		ModelAndView view = new ModelAndView("admin/viewAllJobApplay");

		return view;

	}
	
	@GetMapping("/AllTests") // Company home
	public ModelAndView Tests(Model model) {
		ResponseEntity<Test[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/allTests", Test[].class);
		Test[] responseBody = responseEntity.getBody();
		List<Test> testList = Arrays.asList(responseBody);

		System.out.println("testList" + testList);

		model.addAttribute("testList", testList);
		ModelAndView view = new ModelAndView("admin/viewTests");

		return view;

	}
	@GetMapping("/AllTestResults") // Company home  allRes
	public ModelAndView AllTestResults(Model model) {
		ResponseEntity<TestResult[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/allRes", TestResult[].class);
		TestResult[] responseBody = responseEntity.getBody();
		List<TestResult> testresutList = Arrays.asList(responseBody);

		System.out.println("testresutList" + testresutList);

		model.addAttribute("testresutList", testresutList);
		ModelAndView view = new ModelAndView("admin/viewAllTestResults");

		return view;

	}
	@GetMapping("/AllSelectedCanditates") // Company home
	public ModelAndView AllSelectedCanditates(Model model) {
		ResponseEntity<SelectedCandidates[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/allSelected", SelectedCandidates[].class);
		SelectedCandidates[] responseBody = responseEntity.getBody();
		List<SelectedCandidates> selectedList = Arrays.asList(responseBody);

		System.out.println("selectedList" + selectedList);

		model.addAttribute("selectedList", selectedList);
		ModelAndView view = new ModelAndView("admin/viewSelected");

		return view;

	}
	
}
