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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.JobApply;
import com.example.entity.JobSekers;
import com.example.entity.Questions;
import com.example.entity.TestResult;
import com.example.entity.Vacancy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/jobseeker")
public class TakeExamController {

	@GetMapping("/Exam/{vacancyId}")
	public ModelAndView addJobApplay(@PathVariable("vacancyId") int vacancyId, Model model, HttpSession session) {

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Questions[]> responseEntity1 = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/getQuestions/" + vacancyId, Questions[].class);
		Questions[] responseObj = responseEntity1.getBody();
		List<Questions> questionsList = Arrays.asList(responseObj);

		System.out.println("questionsList=" + questionsList);

		model.addAttribute("questionsList", questionsList);

		session.setAttribute("questionsList", questionsList);

		ModelAndView view = new ModelAndView("jobseeker/takeexam");
		return view;
	}

	@PostMapping("/submitAnswer")
	public String submitAnswer(HttpServletRequest request, HttpSession session,Model model) {
		////////////////////
		JobSekers jobSeeker = (JobSekers) session.getAttribute("jobseeker");
		int userId = jobSeeker.getUserId();
		System.err.println("User ID: " + userId);

		///////////////////////////////////////////////////////////////////////////

		List<Questions> questionsList = (List<Questions>) session.getAttribute("questionsList");

		int totalScore = 0;

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		for (Questions quest : questionsList) {
			int qid = quest.getQuestionId();

			////////////////////////////////////////////////////////////////////////
			Vacancy vacancy = quest.getVacancy();
			////////////////////////////
			System.err.println("vvvvvvvvvvvvvvvvv" + vacancy.getVacancyId());
			String selectedOption = request.getParameter("selectedOptions" + qid);
			System.out.println("qid: " + qid + " selectedoption=" + selectedOption);
			String correctOption = quest.getCorrect();
			System.out.println(
					"qid: " + qid + ", selectedOption: " + selectedOption + ", correctOption: " + correctOption);
			String score = "0";
			String result = "incorrect";
			if (selectedOption != null && selectedOption.equals(correctOption)) {
				result = "correct";
				score = quest.getScore();
				totalScore += Integer.parseInt(quest.getScore());

				// Add score to totalScore

			}

			TestResult testResult = new TestResult();
			testResult.setJobsekers(jobSeeker);
			testResult.setQuestions(quest);
			testResult.setVacancy(vacancy);
			testResult.setSelectedOption(selectedOption);
			testResult.setResult(result);
			// Assuming you calculate score somewhere else and set it here
			testResult.setScore(score);
////////////////////////////////////////////////////////////////////////////////////
			// Save the test result
			// setting test result object to end point

			HttpEntity<TestResult> entity = new HttpEntity<TestResult>(testResult, headers);

			String url = "http://3.108.237.172:8080/HireMeNow/api/v1/addResult";
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

			String responseString = response.getBody();
			System.out.println("responseString" + responseString);

/////////////////////////////////////////////////////////////////

			String status;
			if (totalScore >= 75) {
				status = "qualified";
			} else {
				status = "better luck next time";
			}

			System.out.println("totalscore" + totalScore);

			JobApply jobapply = new JobApply();
			///
			jobapply.setStatus(String.valueOf(totalScore));

			HttpEntity<JobApply> entity1 = new HttpEntity<JobApply>(jobapply, headers);

			String url1 = "http://3.108.237.172:8080/HireMeNow/api/v1/updatestat/" + userId + "/" + vacancy.getVacancyId() + "/"
					+ status + "/" + totalScore;
			ResponseEntity<String> response1 = restTemplate.exchange(url1, HttpMethod.PUT, entity, String.class);

			String responseString1 = response.getBody();
			System.out.println("responseString" + responseString);

		}
		model.addAttribute("RESP", "Results Have Been Submitted Thank You Fo Choosing Us !!");

		return "redirect:/jobseeker/jobHome?message="+model.getAttribute("RESP");
	}
}
