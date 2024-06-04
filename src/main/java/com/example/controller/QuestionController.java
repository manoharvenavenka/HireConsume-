package com.example.controller;

import java.util.Arrays;
import java.util.List;

import org.aspectj.weaver.patterns.TypePatternQuestions.Question;
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
import com.example.entity.Questions;
import com.example.entity.Test;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/company")

public class QuestionController {

	@GetMapping("/addQuestions")
	public ModelAndView Addmhfyi(Model model,HttpSession session) {
		Questions que = new Questions();
		model.addAttribute("questions", que);
///////////////////////////////////////////////////////
		Company company = (Company) session.getAttribute("company");

		int cid = company.getCompanyId();
		//String name =company.getCompanyName();
		System.out.println("companyId:::" + cid);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Object[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/vacancyIdAndJobtitle/" + cid, Object[].class);
		Object[] responseBody = responseEntity.getBody();
		List<Object> vacancyList = Arrays.asList(responseBody);

		System.out.println("chand=" + vacancyList);
		model.addAttribute("vacancyList", vacancyList);
		///////////////////////////////////////////////////////////
		
		ResponseEntity<Test[]> responseEntity1 = restTemplate.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/viewTest/"+cid,
				Test[].class);
		Test[] responseObj = responseEntity1.getBody();
		List<Test> testList = Arrays.asList(responseObj);

		System.out.println("testList" + testList);

		model.addAttribute("testList", testList);
		/////////////////////////////////////////////////////

		ModelAndView view = new ModelAndView("company/addQuestions"); 
		return view;
	}
	@PostMapping("/addingQuestions")
	public String addingQues(@ModelAttribute("questions") Questions questions, Model model) {
		
	
	    
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		HttpEntity<Questions> entity = new HttpEntity<Questions>(questions, headers);

		RestTemplate restTemplate = new RestTemplate();

		String url = "http://3.108.237.172:8080/HireMeNow/api/v1/addQuestions";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		String responseString = response.getBody();
		System.out.println("responseString" + responseString);

		questions = new Questions();

		model.addAttribute("questions", questions);

		
		model.addAttribute("responseString", responseString);
		 model.addAttribute("RESP", "Questions Added successfully.");
			return "redirect:/company/addQuestions?message="+model.getAttribute("RESP");
	}
	
	
	@GetMapping("/viewQuestions")
	public ModelAndView viewQues(Model model,HttpSession session) {
		
		Company company = (Company) session.getAttribute("company");

		int cid = company.getCompanyId();
		System.out.println("company::::::" + cid);
		
		
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Questions[]> responseEntity = restTemplate.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/viewQuestions/"+cid,
				Questions[].class);
		Questions[] responseBody = responseEntity.getBody();
		List<Questions> quesList = Arrays.asList(responseBody);

		System.out.println("quesList" + quesList);

		model.addAttribute("quesList", quesList);
		ModelAndView view = new ModelAndView("company/viewQuestions"); 
		return view;
	}
	
	
	@GetMapping("/editQuestions/{questionId}")
	public ModelAndView editVac(@PathVariable("questionId") int questionId, Model model, HttpSession session) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Questions> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/getQuestionsBy/" + questionId, Questions.class);
		Questions question = responseEntity.getBody();
		System.out.println("question"+question);
		model.addAttribute("questions", question);
		/////////////////////////////////////////////////////////////////////
		Company company = (Company) session.getAttribute("company");

		int cid = company.getCompanyId();
		System.out.println("companyId:::" + cid);


		ResponseEntity<Object[]> responseEntity1 = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/vacancyIdAndJobtitle/" + cid, Object[].class);
		Object[] responseBody = responseEntity1.getBody();
		List<Object> vacancyList = Arrays.asList(responseBody);

		System.out.println("chand=" + vacancyList);
		model.addAttribute("vacancyList", vacancyList);
		///////////////////////////////////////////////////////////
		
		ResponseEntity<Test[]> responseEntity2 = restTemplate.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/viewTest/"+cid,
				Test[].class);
		Test[] responseObj = responseEntity2.getBody();
		List<Test> testList = Arrays.asList(responseObj);

		System.out.println("testList" + testList);

		model.addAttribute("testList", testList);
		ModelAndView view = new ModelAndView("company/editQuestions");

		return view;

	}

	@PostMapping("/updateQues")
	public String updateVac(@ModelAttribute("questions") Questions questions, Model model,RedirectAttributes redirectAttributes) {

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		HttpEntity<Questions> entity = new HttpEntity<Questions>(questions, headers);

		RestTemplate restTemplate = new RestTemplate();

		String url = "http://3.108.237.172:8080/HireMeNow/api/v1/updateQuestions/" + questions.getQuestionId();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

		String responseString = response.getBody();
		System.out.println("responseString" + responseString);
		 model.addAttribute("RESP", "Questions Updated successfully.");
		return "redirect:/company/viewQuestions?message="+model.getAttribute("RESP");
	}


}
