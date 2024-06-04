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
import com.example.entity.Vacancy;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/company")
public class VacancyController {
	@GetMapping("/addVecency")
	public ModelAndView AddVecency(Model model, HttpSession session) {
		Vacancy vacancy = new Vacancy();
		model.addAttribute("vacancy", vacancy);

		Company company = (Company) session.getAttribute("company");

		int cid = company.getCompanyId();
		System.out.println("companyId:::" + cid);
		
		model.addAttribute("companyId", company.getCompanyId());

		ModelAndView view = new ModelAndView("company/addVecency");
		return view;
	}

	@PostMapping("/addingVacancy")
	public String AddVecency(@ModelAttribute("vacancy") Vacancy vacancy, Model model, HttpSession session,RedirectAttributes redirectAttributes) {
		
	
	    
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		HttpEntity<Vacancy> entity = new HttpEntity<Vacancy>(vacancy, headers);

		RestTemplate restTemplate = new RestTemplate();

		String url = "http://3.108.237.172:8080/HireMeNow/api/v1/addVacancy";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		String responseString = response.getBody();
		System.out.println("responseString" + responseString);

		vacancy = new Vacancy();

		model.addAttribute("vacancy", vacancy);

		model.addAttribute("responseString", responseString);
		 model.addAttribute("RESP","VACANCY ADDED SUCCESSFULLY!!!!");
		return "redirect:/company/addVecency?message="+model.getAttribute("RESP");
	}

	@GetMapping("/viewVecencies")
	public ModelAndView viewVecncies(Model model,HttpSession session) {
		Company company = (Company) session.getAttribute("company");

		int cid = company.getCompanyId();
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Vacancy[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/getVacancies/"+cid, Vacancy[].class);
		Vacancy[] responseBody = responseEntity.getBody();
		List<Vacancy> vacancylist = Arrays.asList(responseBody);

		model.addAttribute("vacancylist", vacancylist);
		ModelAndView view = new ModelAndView("company/viewVacancies");
		return view;
	}
	

	@GetMapping("/editVacancy/{vacancyId}")
	public ModelAndView editVac(@PathVariable("vacancyId") int vacancyId,Model model) {
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Vacancy> responseEntity = 
				restTemplate.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/getVacancy/" +vacancyId,Vacancy.class);
		Vacancy vacancy = responseEntity.getBody();
		System.out.println("jobtit = " +vacancy.getJobTitle());
		model.addAttribute("vacancy",vacancy);
		ModelAndView view = new ModelAndView("company/editVacancy");
		
		return view;
		
	}

	@PostMapping("/updateVacancy")
	public String updateVac(@ModelAttribute("vacancy") Vacancy vacancy, Model model,HttpSession session,RedirectAttributes redirectAttributes) {
		int companyId = ((Company) session.getAttribute("company")).getCompanyId();
	      System.out.println("mmmmmmm="+companyId);
	   // Check if the Company object in the Vacancy is null, if so, initialize it
	      if (vacancy.getCompany() == null) {
	          vacancy.setCompany(new Company());
	      }
	      
	      // Set companyId in the Vacancy object's company attribute
	      vacancy.getCompany().setCompanyId(companyId);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		HttpEntity<Vacancy> entity = new HttpEntity<Vacancy>(vacancy, headers);

		RestTemplate restTemplate = new RestTemplate();

		String url = "http://3.108.237.172:8080/HireMeNow/api/v1/updateVacancy/"+vacancy.getVacancyId();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

		String responseString = response.getBody();
		System.out.println("responseString" + responseString);

		model.addAttribute("RESP", "Vacancy updated successfully.");

		 return "redirect:/company/viewVecencies?message=" + model.getAttribute("RESP");
	}
}
