package com.example.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.Company;
import com.example.entity.Documents;
import com.example.entity.JobApply;
import com.example.entity.SelectedCandidates;
import com.example.entity.User;
import com.example.entity.Vacancy;
import com.example.repository.UserRepository;
import com.example.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/company")
public class CompanyController {

	@Autowired
	UserService userService;
	
	
	@GetMapping("/companyHome") // Company home
	public ModelAndView CompanyHome(Model model, HttpSession session, Authentication authentication, Company company) {
		String Username = authentication.getName();
		System.out.println("namw=" + Username);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		HttpEntity<Company> entity = new HttpEntity<Company>(company, headers);

		RestTemplate restTemplate = new RestTemplate();
		System.out.println("hello");
		ResponseEntity<Company> response = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/companyEmail/" + Username, Company.class);

		Company responseObject = response.getBody();

		System.out.println("responseFlag=" + responseObject);

		if (responseObject != null) { // login success

			System.out.println("sindu=" + responseObject.getCompanyId());
			session.setAttribute("company", responseObject);

			Company company1 = (Company) session.getAttribute("company");

			int companyid = company1.getCompanyId();

			model.addAttribute("companyId", companyid);

			ModelAndView view = new ModelAndView("company/companyhome");

			return view;

		} else {

			ModelAndView view = new ModelAndView("loginpage");

			return view;

		}

	}

	@GetMapping("/getProfile")
	public ModelAndView getProfile(Model model, HttpSession session) {
		Company company = (Company) session.getAttribute("company");

		int cid = company.getCompanyId();
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Company> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/getCompany/" + cid, Company.class);
		Company company1 = responseEntity.getBody();

		System.out.println("company = " + company1);
		model.addAttribute("company", company1);
		
		Company comp=new Company();
		model.addAttribute("comp", comp);
		ModelAndView view = new ModelAndView("company/profile");

		return view;

	}
	

	@GetMapping("/viewApply")
	public ModelAndView viewQues(Model model, HttpSession session) {

		Company company = (Company) session.getAttribute("company");

		int cid = company.getCompanyId();
		System.out.println("company::::::" + cid);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<JobApply[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/viewApplies/" + cid, JobApply[].class);
		JobApply[] responseBody = responseEntity.getBody();
		List<JobApply> applyList = Arrays.asList(responseBody);
		model.addAttribute("applyList", applyList);
		for (JobApply jooo : applyList) {
			System.out.println("uuuuuuuuuuu" + jooo.getJobsekers().getUserId());

			ResponseEntity<Documents[]> responseEntity1 = restTemplate.getForEntity(
					"http://3.108.237.172:8080/HireMeNow/api/v1/getDocuments/" + jooo.getJobsekers().getUserId(), Documents[].class);
			Documents[] responseBody1 = responseEntity1.getBody();
			List<Documents> documentsList = Arrays.asList(responseBody1);

			model.addAttribute("documentsList", documentsList);
			System.err.println("documentsList"+documentsList);
			/////////////////////////////////////////////

			///////////////////////////////////////////////////////////////////////////
			int userid = jooo.getJobsekers().getUserId();
			int vacancyId=jooo.getVacancy().getVacancyId();
			ResponseEntity<Boolean> responseEntity2 = restTemplate
					.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/checkSlected/" + userid+"/"+vacancyId, Boolean.class);
			Boolean selected = responseEntity2.getBody();
			session.setAttribute("selected_" + userid, selected);

		}
		System.out.println("applyList" + applyList);
		////////////////////////////////////////////////////////

		//

		ModelAndView view = new ModelAndView("company/viewJobApplies");
		return view;
	}
///////
	
	
	
	
	
	
	
	
	
	
	@PostMapping("/updatePic")
	public String addProducts(@ModelAttribute("company") Company company,
			@RequestParam("logo1") MultipartFile logo1, Model model,HttpSession session) {
		
		   // Encrypt the password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(company.getPassword());

        // Get the existing user by email
        User existingUser = userService.getUserByEmail(company.getEmail());

        if (existingUser != null) {
            // Update the existing user entity with new information
            existingUser.setPassword(encryptedPassword);
            existingUser.setRole("ROLE_COMPANY"); // Assuming role is also updated

            // Save the updated user entity
            userService.updateCompany(existingUser);
            System.out.println("User updated successfully");
        } else {
            System.out.println("User not found");
        }
        
        
        

		try {

			// Create the upload directory if not exists
			Files.createDirectories(Path.of("./temp_uploads"));

			byte[] picBytes = logo1.getBytes();
			String companyPicName = "temp_" + System.currentTimeMillis() + "_" + logo1.getOriginalFilename();
			Files.write(Paths.get("./temp_uploads", companyPicName), picBytes);

			// Set the product picture name in the product object
			company.setLogo(companyPicName);

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

			HttpEntity<Company> entity = new HttpEntity<Company>(company, headers);

			RestTemplate restTemplate = new RestTemplate();

			String url = "http://3.108.237.172:8080/HireMeNow/api/v1/updateCompany/"+company.getCompanyId();
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

			// System.out.println("responseFlag=" + responseFlag);

			// Up to here product is adding

			//////////////////////// code to upload product picture
			HttpHeaders picHeaders = new HttpHeaders();
			picHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<String, Object> picMap = new LinkedMultiValueMap<>();
			picMap.add("file", new FileSystemResource("./temp_uploads/" + companyPicName));
			picMap.add("filename", companyPicName);
			HttpEntity<MultiValueMap<String, Object>> picEntity = new HttpEntity<>(picMap, picHeaders);

			// Send a request to upload the product picture
			String uploadPicUrl = "http://3.108.237.172:8080/HireMeNow/api/files/upload";
			ResponseEntity<String> picResponse = restTemplate.exchange(uploadPicUrl, HttpMethod.POST, picEntity,
					String.class);

			String picResponseString = picResponse.getBody();

			// Clean up: Delete the temporary product picture
			Files.deleteIfExists(Paths.get("./temp_uploads", companyPicName));

			///////////////////////

			company = new Company();
			model.addAttribute("comp", company);
			
			model.addAttribute("RESP", "Details Updated successfully.");

			return "redirect:/company/getProfile?message="+model.getAttribute("RESP");

		} catch (IOException e) {
			// Handle exceptions related to file operations or HTTP requests

			
			return null;
		}

	}
	
	
	
	@GetMapping("/deleteQuestion/{questionId}")
	public String deleteQues(@PathVariable("questionId") int questionId,Model model) {
	
		Map<String, Integer> pathVar = new HashMap<>();
		pathVar.put("questionId", questionId);
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.delete("http://3.108.237.172:8080/HireMeNow/api/v1/deleteQues/{questionId}",pathVar);
		//ModelAndView view = new ModelAndView("viewProducts");
		//return view;
		 model.addAttribute("RESP","Questions Deleted SuccessFully!");

		return "redirect:/company/viewQuestions?message="+model.getAttribute("RESP");
		
	}
	@GetMapping("/deletevacancy/{vacancyId}")
	public String deletevac(@PathVariable("vacancyId") int vacancyId,Model model) {
	
		Map<String, Integer> pathVar = new HashMap<>();
		pathVar.put("vacancyId", vacancyId);
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.delete("http://3.108.237.172:8080/HireMeNow/api/v1/deleteVacancy/{vacancyId}",pathVar);
		//ModelAndView view = new ModelAndView("viewProducts");
		//return view;
		model.addAttribute("RESP","Vacancy Deleted SuccessFully!");

		return "redirect:/company/viewVecencies?message="+model.getAttribute("RESP");
		
	}
	@GetMapping("/deletetest/{testId}")
	public String deleteTest(@PathVariable("testId") int testId,Model model) {
	
		Map<String, Integer> pathVar = new HashMap<>();
		pathVar.put("testId", testId);
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.delete("http://3.108.237.172:8080/HireMeNow/api/v1/deleteTest/{testId}",pathVar);
		//ModelAndView view = new ModelAndView("viewProducts");
		//return view;
		 model.addAttribute("RESP","Test Deleted SuccessFully!");

		return "redirect:/company/viewTest?message="+model.getAttribute("RESP");
		
	}
	

	@GetMapping("/selectedCanditates")
	public ModelAndView viewSelected(Model model,HttpSession session) {
		Company company = (Company) session.getAttribute("company");

		int cid = company.getCompanyId();
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<SelectedCandidates[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/getSelected/"+cid, SelectedCandidates[].class);
		SelectedCandidates[] responseBody = responseEntity.getBody();
		List<SelectedCandidates> selectedList = Arrays.asList(responseBody);
System.out.println("selectedList="+selectedList);
		model.addAttribute("selectedList", selectedList);
		ModelAndView view = new ModelAndView("company/viewResults");
		return view;
	}
}
