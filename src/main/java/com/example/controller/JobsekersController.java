package com.example.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.entity.Company;
import com.example.entity.JobSekers;
import com.example.entity.Test;
import com.example.entity.User;
import com.example.entity.Vacancy;
import com.example.service.UserService;

import jakarta.security.auth.message.callback.PrivateKeyCallback.Request;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/jobseeker")
public class JobsekersController {
	
	
	@Autowired
	UserService userService;
	
	
	@GetMapping("/jobHome") // job home
	public ModelAndView CompanyHome(Model model, HttpSession session, Authentication authentication,
			JobSekers jobseekers) {
		///////////////////////////////////////////////////////////////////////////////////////
		String Username = authentication.getName();
		System.out.println("namw=" + Username);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		HttpEntity<JobSekers> entity = new HttpEntity<JobSekers>(jobseekers, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<JobSekers> response = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/jobEmail/" + Username, JobSekers.class);

		JobSekers responseObject = response.getBody();

		System.out.println("responseFlag=" + responseObject);

		if (responseObject != null) { // login success

			System.out.println("sindu=" + responseObject.getUserId());
			session.setAttribute("jobseeker", responseObject);

			JobSekers job1 = (JobSekers) session.getAttribute("jobseeker");

			int userid = job1.getUserId();

			model.addAttribute("userId", userid);
			/////////////////////////////////////////////////////////////////////////////

			ResponseEntity<Vacancy[]> responseEntity1 = restTemplate
					.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/allVacancies", Vacancy[].class);
			Vacancy[] responseObj = responseEntity1.getBody();
			List<Vacancy> vacancyList = Arrays.asList(responseObj);
			System.out.println("vacancyList" + vacancyList);
			model.addAttribute("vacancyList", vacancyList);
			///////////////////////////////////////////////////////
			 for (Vacancy vacancy : vacancyList) {
	                int vacancyId = vacancy.getVacancyId();
	                ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity(
	                        "http://3.108.237.172:8080/HireMeNow/api/v1/check/" + vacancyId + "/" + userid, Boolean.class);
	                Boolean applied = responseEntity.getBody();
	                session.setAttribute("applied_" + vacancyId, applied);
	            }

			ModelAndView view = new ModelAndView("jobseeker/jobseekerhome");

			return view;

		} else {

			ModelAndView view = new ModelAndView("loginpage");

			return view;

		}

	}

	@GetMapping("/jobProfile")
	public ModelAndView jobprof(Model model, HttpSession session) {
		
		
		JobSekers job1 = (JobSekers) session.getAttribute("jobseeker");
		//System.err.println("joo" + job1.getFirstName());
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<JobSekers> responseEntity = 
				restTemplate.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/getJobsekers/" +job1.getUserId(),JobSekers.class);
		JobSekers jobsek1 = responseEntity.getBody();
		//System.out.println("company = " +company1.getCompanyName());
		model.addAttribute("jobseeker",jobsek1);
		ModelAndView view = new ModelAndView("jobseeker/jobProfile");
		return view;
	}
	
	
	@PostMapping("/updateJob")
	public String addProducts(@ModelAttribute("jobseeker") JobSekers jobseeker,
			@RequestParam("logo1") MultipartFile logo1, Model model,HttpSession session,RedirectAttributes redirectAttributes) {
		// Set the product picture name and save it to a temporary location
		 BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	        String encryptedPassword = passwordEncoder.encode(jobseeker.getPassword());

	        // Get the existing user by email
	        User existingUser = userService.getUserByEmail(jobseeker.getEmail());

	        if (existingUser != null) {
	            // Update the existing user entity with new information
	            existingUser.setPassword(encryptedPassword);
	            existingUser.setRole("ROLE_JOBSEEKER"); // Assuming role is also updated

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
			jobseeker.setProfilePic(companyPicName);

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

			HttpEntity<JobSekers> entity = new HttpEntity<JobSekers>(jobseeker, headers);

			RestTemplate restTemplate = new RestTemplate();

			String url = "http://3.108.237.172:8080/HireMeNow/api/v1/updateJobseker/"+jobseeker.getUserId();
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

			jobseeker = new JobSekers();
			model.addAttribute("jobseeker", jobseeker);
			
			model.addAttribute("RESP", "Details Updated SuccessFully!");
			return "redirect:/jobseeker/jobProfile?message="+model.getAttribute("RESP");

		} catch (IOException e) {
			// Handle exceptions related to file operations or HTTP requests

			
			return null;
		}

	}
	
}
