package com.example.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.entity.Admin;
import com.example.entity.Company;
import com.example.entity.JobSekers;
import com.example.entity.User;
import com.example.repository.UserRepository;

@Controller
public class GlobalController {
	@Autowired
	UserRepository userrepo;

	@GetMapping("/index")
	public String getMethodName() {
		return "index";
	}
	@GetMapping("/Contact")
	public String gete() {
		return "Contact";
	}
	
	@GetMapping("/About")
	public String getMethod() {
		return "About";
	}

	@GetMapping("/addCompany")
	public ModelAndView addCompany(Model model) {
		Company company = new Company();

		model.addAttribute("company", company);
		ModelAndView view = new ModelAndView("company/addcompany");
		return view;
	}
	
	@GetMapping("/addAdmin")
	public ModelAndView addAdmin(Model model) {
		Admin admin = new Admin();

		model.addAttribute("admin", admin);
		ModelAndView view = new ModelAndView("admin/AdminReg");
		return view;
	}

	@PostMapping("/addAdmin")
	public String addAdmin(@ModelAttribute("admin") Admin admin, Model model) {
		// Set the product picture name and save it to a temporary location

		BCryptPasswordEncoder pswdEncoder = new BCryptPasswordEncoder();
		String encriptpwd = pswdEncoder.encode(admin.getAdmPassword());
		User user = new User();
		user.setPassword(encriptpwd);
		user.setUsername(admin.getAdminName());
		String Role = "ROLE_ADMIN";
		user.setRole(Role);
		userrepo.save(user);
		return "redirect:/index";
		
	}
	
	
	@PostMapping("/addingCompany")
	public ModelAndView addProducts(@ModelAttribute("company") Company company,
			@RequestParam("logo1") MultipartFile logo1, Model model) {
		// Set the product picture name and save it to a temporary location

		BCryptPasswordEncoder pswdEncoder = new BCryptPasswordEncoder();
		String encriptpwd = pswdEncoder.encode(company.getPassword());
		User user = new User();
		user.setPassword(encriptpwd);
		user.setUsername(company.getEmail());
		String Role = "ROLE_COMPANY";
		user.setRole(Role);
		userrepo.save(user);
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
			String url = "http://3.108.237.172:8080/HireMeNow/api/v1/addCompany";
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			String responseString = response.getBody();
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
			model.addAttribute("company", company);
			ModelAndView view = new ModelAndView("loginpage");
			model.addAttribute("responseString", responseString);

			return view;

		} catch (IOException e) {
			// Handle exceptions related to file operations or HTTP requests

			ModelAndView errorView = new ModelAndView("/userlogin");
			model.addAttribute("responseString", e.getMessage());
			return errorView;
		}

	}

	@GetMapping("/addJobsekers")
	public ModelAndView addJob(Model model) {
		JobSekers jobsekers = new JobSekers();

		model.addAttribute("jobsekers", jobsekers);
		ModelAndView view = new ModelAndView("jobseeker/addjobseekers");
		return view;
	}

	@PostMapping("/addingJobsekers")
	public ModelAndView addJobsekers(@ModelAttribute("jobsekers") JobSekers jobsekers,
			@RequestParam("profilePic1") MultipartFile profilePic1, Model model) {
		BCryptPasswordEncoder pswdEncoder = new BCryptPasswordEncoder();
		String encriptpwd = pswdEncoder.encode(jobsekers.getPassword());
		User user = new User();
		user.setPassword(encriptpwd);
		user.setUsername(jobsekers.getEmail());
		String Role = "ROLE_JOBSEEKER";
		user.setRole(Role);
		userrepo.save(user);
		// Set the product picture name and save it to a temporary location
		try {

			// Create the upload directory if not exists
			// Files.createDirectories(Path.of("./temp_uploads"));

			byte[] picBytes = profilePic1.getBytes();
			String profilePicName = "temp_" + System.currentTimeMillis() + "_" + profilePic1.getOriginalFilename();
			Files.write(Paths.get("./temp_uploads", profilePicName), picBytes);

			// Set the product picture name in the product object
			jobsekers.setProfilePic(profilePicName);

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<JobSekers> entity = new HttpEntity<JobSekers>(jobsekers, headers);
			RestTemplate restTemplate = new RestTemplate();
			String url = "http://3.108.237.172:8080/HireMeNow/api/v1/addJobseker";
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			String responseString = response.getBody();
			// System.out.println("responseFlag=" + responseFlag);

			// Up to here product is adding

			//////////////////////// code to upload product picture
			HttpHeaders picHeaders = new HttpHeaders();
			picHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<String, Object> picMap = new LinkedMultiValueMap<>();
			picMap.add("file", new FileSystemResource("./temp_uploads/" + profilePicName));
			picMap.add("filename", profilePicName);
			HttpEntity<MultiValueMap<String, Object>> picEntity = new HttpEntity<>(picMap, picHeaders);

			// Send a request to upload the product picture
			String uploadPicUrl = "http://3.108.237.172:8080/HireMeNow/api/files/upload";
			ResponseEntity<String> picResponse = restTemplate.exchange(uploadPicUrl, HttpMethod.POST, picEntity,
					String.class);

			String picResponseString = picResponse.getBody();

			// Clean up: Delete the temporary product picture
			Files.deleteIfExists(Paths.get("./temp_uploads", profilePicName));

			///////////////////////

			jobsekers = new JobSekers();
			model.addAttribute("jobsekers", jobsekers);
			ModelAndView view = new ModelAndView("loginpage");
			model.addAttribute("responseString", responseString);

			return view;

		} catch (IOException e) {
			// Handle exceptions related to file operations or HTTP requests

			ModelAndView errorView = new ModelAndView("jobseeker/addjobseeker");
			model.addAttribute("responseString", e.getMessage());
			return errorView;
		}

	}
}
