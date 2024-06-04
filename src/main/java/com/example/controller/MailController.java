package com.example.controller;

import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.EMail;
import com.example.entity.JobApply;
import com.example.entity.SelectedCandidates;
import com.example.entity.Test;


import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/company")
public class MailController {

	
	@PostMapping("/submitdetails")
	public String sendMail(@ModelAttribute("jobapply") JobApply jobapply, Model model, HttpSession session,RedirectAttributes redirectAttributes) {
		
		
		int jobapply1=jobapply.getApplyId();
		
//		System.err.println(jobapply1);
//		System.out.println("nithin="+jobapply.getFinalscore());
		/////////////////////////////////////////////////////
//		based on job applay id we are getting details of user 
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<JobApply> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/getApplicants/" + jobapply1, JobApply.class);
		JobApply jobapp = responseEntity.getBody();

		System.out.println("company = " + jobapp.getJobsekers().getFirstName()+"lastname="+jobapp.getJobsekers().getLastName());
		/////////////////////////////////////////////////
		//Based on vacancyId we are getting Test object 
		ResponseEntity<Test> responseEntity1 = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/getTestByVacancyId/" + jobapp.getVacancy().getVacancyId(), Test.class);
		Test testobj = responseEntity1.getBody();
		System.err.println("tst vacancy"+testobj.getTestId());
		//////////////////////////////////////////////////////
		SelectedCandidates selected= new SelectedCandidates();
		selected.setJobSekers(jobapp.getJobsekers());
		selected.setVacancy(jobapp.getVacancy());
		selected.setTest(testobj);
		selected.setScore(Integer.parseInt(jobapp.getFinalscore()));
		selected.setStatus(jobapp.getStatus());
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<SelectedCandidates> entity = new HttpEntity<SelectedCandidates>(selected, headers);

		String url = "http://3.108.237.172:8080/HireMeNow/api/v1/addSelectedCanditates";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		String responseString = response.getBody();
		System.out.println("responseString" + responseString);
		
		////////////////////////////////////////
		
		String firstName = jobapp.getJobsekers().getFirstName();
		String lastName = jobapp.getJobsekers().getLastName();
		
		// Assuming jobapp.getVacancy().getCompany().getLogo() returns the filename of the logo image
		String logoFileName = jobapp.getVacancy().getCompany().getLogo();

		// Assuming the logo image is hosted on the same server as your application
		String logoUrl = "http://3.108.237.172:8080/HireMeNow/uploads/" + logoFileName;

		String body = "<html><head>";
		body += "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\">";
		body += "<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css\">";
		body += "</head><body>";
		body += "<div class=\"container\">";
		body += "<div class=\"card mt-5\">";
		body += "<div class=\"card-body\">";
		body += "<h2 class=\"card-title\">Congratulations, " + firstName + " " + lastName + "!</h2>";
		body += "<p class=\"card-text\">We are thrilled to offer you the position of "+jobapp.getVacancy().getJobTitle()+" at "+jobapp.getVacancy().getCompany().getCompanyName()+".</p>";
		body += "<p class=\"card-text\">As part of our team, you will enjoy a range of benefits, including health insurance, retirement plans, and more.</p>";
		body += "<p class=\"card-text\">Your role will involve "+jobapp.getVacancy().getDescription()+". We have a comprehensive onboarding process in place to ensure a smooth transition into your new role.</p>";
		body += "<p class=\"card-text\">You will be joining a dynamic team of professionals, including Manohar in various roles.</p>";
		body += "<p class=\"card-text\">At "+jobapp.getVacancy().getCompany().getCompanyName()+", we are committed to your professional growth and offer opportunities for career advancement through Career Development Programs.</p>";
		body += "<p class=\"card-text\">We are proud of our recent achievements, including "+jobapp.getVacancy().getCompany().getCompanyLevel()+", and look forward to achieving even more with your contribution.</p>";
		body += "<p class=\"card-text\">Connect with us on social media:</p>";
		body += "<ul class=\"list-unstyled\">";
		body += "<li><a href=\"https://twitter.com/?lang=en-in\"><i class=\"bi bi-twitter\"></i> Twitter</a></li>";
		body += "<li><a href=\"https://www.linkedin.com/login\"><i class=\"bi bi-linkedin\"></i> LinkedIn</a></li>";
		body += "<li><a href=\"https://www.facebook.com/\"><i class=\"bi bi-facebook\"></i> Facebook</a></li>";
		body += "</ul>";
		body += "<p class=\"card-text\">Final Score: " + jobapp.getFinalscore() + "</p>";

		body += "<p class=\"card-text\">If you have any questions, please refer to our FAQs <a href=\"" + jobapp.getVacancy().getCompany().getUrl() + "\">here</a> or feel free to reach out to our HR department at "+jobapp.getVacancy().getCompany().getUrl()+" or "+jobapp.getVacancy().getCompany().getMobile()+".</p>";
		body += "<p class=\"card-text\">Next steps include signing the offer letter, completing background checks, and scheduling a start date. We will guide you through this process.</p>";
	
		
		body += "<img src=\"" + logoUrl + "\" alt=\"Company Logo\" class=\"img-fluid\" style=\"width: 100px;\">";


		body += "<p class=\"card-text mt-5\">Regards,<br>CEO/Management<br>"+jobapp.getVacancy().getCompany().getCompanyName()+"</p>";
		body += "</div>";
		body += "</div>";
		
		body += "</div>";
		body += "</body></html>";


		String subject = "Congratulations on Your Job Offer at "+jobapp.getVacancy().getCompany().getCompanyName()+"";

		String email=jobapp.getJobsekers().getEmail();
		EMail mail= new EMail();
		mail.setToMail(email);
		mail.setSubject(subject);
		mail.setBody(body);
		

	    HttpHeaders headers1 = new HttpHeaders();
	    headers1.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

	    HttpEntity<EMail> entity1 = new HttpEntity<EMail>(mail, headers1);

	    RestTemplate restTemplate1 = new RestTemplate();

	    String url1 = "http://3.108.237.172:8080/HireMeNow/sendMail";
	    ResponseEntity<String> response1 = restTemplate1.exchange(url1, HttpMethod.POST, entity1, String.class);
	    String responseString1 = response1.getBody();
	     model.addAttribute("RESP", "Selected SuccessFully Mail Has Been Sent!");
		return "redirect:/company/viewApply?message="+model.getAttribute("RESP");
		
}
}
