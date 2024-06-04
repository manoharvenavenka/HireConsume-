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
import com.example.entity.Test;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/company")

public class TestController {

	@GetMapping("/addTest")
	public ModelAndView AddTest(Model model, HttpSession session) {
		Test test = new Test();
		model.addAttribute("test", test);
		Company company = (Company) session.getAttribute("company");

		int cid = company.getCompanyId();
		System.out.println("companyId:::" + cid);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Object[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/vacancyIdAndJobtitle/" + cid, Object[].class);
		Object[] responseBody = responseEntity.getBody();
		List<Object> vacancyList = Arrays.asList(responseBody);

		System.out.println("chand=" + vacancyList);
		model.addAttribute("vacancyList", vacancyList);
		ModelAndView view = new ModelAndView("company/addTest");
		return view;
	}

	@PostMapping("/addingTest")
	public String postMethodName(@ModelAttribute("test") Test test, Model model) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		HttpEntity<Test> entity = new HttpEntity<Test>(test, headers);

		RestTemplate restTemplate = new RestTemplate();

		String url = "http://3.108.237.172:8080/HireMeNow/api/v1/addTest";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		String responseString = response.getBody();
		System.out.println("responseString" + responseString);

		test = new Test();

		model.addAttribute("test", test);
		
		model.addAttribute("responseString", responseString);
		model.addAttribute("RESP", "*Test ADDED");
		return "redirect:/company/addTest?message="+model.getAttribute("RESP");
	}

	@GetMapping("/viewTest")
	public ModelAndView viewTests(Model model, HttpSession session) {

		Company company = (Company) session.getAttribute("company");

		int cid = company.getCompanyId();
		System.out.println("company::::::" + cid);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Test[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/viewTest/" + cid, Test[].class);
		Test[] responseBody = responseEntity.getBody();
		List<Test> testList = Arrays.asList(responseBody);

		System.out.println("testList" + testList);

		model.addAttribute("testList", testList);
		ModelAndView view = new ModelAndView("company/viewTests");
		return view;
	}

	@GetMapping("/editTest/{testId}")
	public ModelAndView editVac(@PathVariable("testId") int testId, Model model, HttpSession session) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Test> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/getTest/" + testId, Test.class);
		Test test = responseEntity.getBody();
		model.addAttribute("test", test);
		Company company = (Company) session.getAttribute("company");

		int cid = company.getCompanyId();
		System.out.println("companyId:::" + cid);

		ResponseEntity<Object[]> responseEntity1 = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/vacancyIdAndJobtitle/" + cid, Object[].class);
		Object[] responseBody = responseEntity1.getBody();
		List<Object> vacancyList = Arrays.asList(responseBody);

		System.out.println("chand=" + vacancyList);
		model.addAttribute("vacancyList", vacancyList);
		ModelAndView view = new ModelAndView("company/editTest");

		return view;

	}

	@PostMapping("/updateTest")
	public String updateVac(@ModelAttribute("test") Test test, Model model) {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		HttpEntity<Test> entity = new HttpEntity<Test>(test, headers);

		RestTemplate restTemplate = new RestTemplate();

		String url = "http://3.108.237.172:8080/HireMeNow/api/v1/updateTest/" + test.getTestId();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

		String responseString = response.getBody();
		System.out.println("responseString" + responseString);
		model.addAttribute("RESP", "Test updated successfully.");
		return "redirect:/company/viewTest?message="+model.getAttribute("RESP");
	}

}
