package com.example.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.example.entity.Documents;
import com.example.entity.JobSekers;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/jobseeker")

public class DocumentController {
	@GetMapping("/addDocuments")
	public ModelAndView addDoc(Model model, HttpSession session) {

		JobSekers job1 = (JobSekers) session.getAttribute("jobseeker");
		int userId = job1.getUserId();

		Documents Documents = new Documents();
		model.addAttribute("userId", userId);
		model.addAttribute("Documents", Documents);
		ModelAndView view = new ModelAndView("jobseeker/addDocuments");
		return view;
	}

	@PostMapping("/addingDoc")
	public String postMethodName(@ModelAttribute("Documents") Documents documents,
			@RequestParam("doc1") MultipartFile doc1, Model model,HttpSession session) {
		JobSekers job1 = (JobSekers) session.getAttribute("jobseeker");
	//	int userId = job1.getUserId();
		documents.setJobsekers(job1);
		
		try {

			// Create the upload directory if not exists
			Files.createDirectories(Path.of("./temp_uploads"));

			byte[] picBytes = doc1.getBytes();
			String documentFileName = "temp_" + System.currentTimeMillis() + "_" + doc1.getOriginalFilename();
			Files.write(Paths.get("./temp_uploads", documentFileName), picBytes);

			// Set the product picture name in the product object
			documents.setDocumentFile(documentFileName);

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

			HttpEntity<Documents> entity = new HttpEntity<Documents>(documents, headers);

			RestTemplate restTemplate = new RestTemplate();

			String url = "http://3.108.237.172:8080/HireMeNow/api/v1/addDocuments";
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

			String responseString = response.getBody();

			System.out.println("responseString" + responseString);

////////////////////////code to upload product picture
			HttpHeaders picHeaders = new HttpHeaders();
			picHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<String, Object> picMap = new LinkedMultiValueMap<>();
			picMap.add("file", new FileSystemResource("./temp_uploads/" + documentFileName));
			picMap.add("filename", documentFileName);
			HttpEntity<MultiValueMap<String, Object>> picEntity = new HttpEntity<>(picMap, picHeaders);

// Send a request to upload the product picture
			String uploadPicUrl = "http://3.108.237.172:8080/HireMeNow/api/files/upload";
			ResponseEntity<String> picResponse = restTemplate.exchange(uploadPicUrl, HttpMethod.POST, picEntity,
					String.class);

			String picResponseString = picResponse.getBody();

// Clean up: Delete the temporary product picture
			Files.deleteIfExists(Paths.get("./temp_uploads", documentFileName));

			documents = new Documents();

			model.addAttribute("Documents", documents);

			model.addAttribute("responseString", responseString);
			model.addAttribute("job", "*ThankYou for Submiting");
			return "redirect:/jobseeker/addDocuments?message="+model.getAttribute("job");
		} catch (IOException e) {
			// Handle exceptions related to file operations or HTTP requests

			model.addAttribute("responseString", e.getMessage());
			model.addAttribute("RESP","Documents Failed!");
			return "redirect:/jobseeker/addDocuments";
		}

	}

	@GetMapping("/viewDoc")
	public ModelAndView Documentsss(Model model, HttpSession session) {

		JobSekers job1 = (JobSekers) session.getAttribute("jobseeker");

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Documents[]> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/getDocuments/" + job1.getUserId(), Documents[].class);
		Documents[] responseBody = responseEntity.getBody();
		List<Documents> documentsList = Arrays.asList(responseBody);

		model.addAttribute("documentsList", documentsList);
		ModelAndView view = new ModelAndView("jobseeker/viewDocuments");
		return view;
	}
	
	@GetMapping("/editDoc/{docId}")
	public ModelAndView GetDoc(@PathVariable("docId")int docId,Model model, HttpSession session) {

		

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Documents> responseEntity = restTemplate
				.getForEntity("http://3.108.237.172:8080/HireMeNow/api/v1/getDoc/" + docId, Documents.class);
		Documents responseBody = responseEntity.getBody();
System.out.println("ssssssssssss"+responseBody);
		model.addAttribute("documents", responseBody);
		ModelAndView view = new ModelAndView("jobseeker/editDoc");
		return view;
	}
	
	@PostMapping("/updateDoc")
	public String addProducts(@ModelAttribute("documents") Documents documents,
			@RequestParam("doc1") MultipartFile doc1, Model model,HttpSession session,RedirectAttributes redirectAttributes) {
		// Set the product picture name and save it to a temporary location
		
		
		try {

			// Create the upload directory if not exists
			Files.createDirectories(Path.of("./temp_uploads"));

			byte[] picBytes = doc1.getBytes();
			String docFileName = "temp_" + System.currentTimeMillis() + "_" + doc1.getOriginalFilename();
			Files.write(Paths.get("./temp_uploads", docFileName), picBytes);

			// Set the product picture name in the product object
			documents.setDocumentFile(docFileName);

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

			HttpEntity<Documents> entity = new HttpEntity<Documents>(documents, headers);

			RestTemplate restTemplate = new RestTemplate();

			String url = "http://3.108.237.172:8080/HireMeNow/api/v1/updateDocuments/"+documents.getDocId();
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

			// System.out.println("responseFlag=" + responseFlag);

			// Up to here product is adding

			//////////////////////// code to upload product picture
			HttpHeaders picHeaders = new HttpHeaders();
			picHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<String, Object> picMap = new LinkedMultiValueMap<>();
			picMap.add("file", new FileSystemResource("./temp_uploads/" + docFileName));
			picMap.add("filename", docFileName);
			HttpEntity<MultiValueMap<String, Object>> picEntity = new HttpEntity<>(picMap, picHeaders);

			// Send a request to upload the product picture
			String uploadPicUrl = "http://3.108.237.172:8080/HireMeNow/api/files/upload";
			ResponseEntity<String> picResponse = restTemplate.exchange(uploadPicUrl, HttpMethod.POST, picEntity,
					String.class);

			String picResponseString = picResponse.getBody();

			// Clean up: Delete the temporary product picture
			Files.deleteIfExists(Paths.get("./temp_uploads", docFileName));

			///////////////////////

			
			
			model.addAttribute("RESP","Documents Updated SuccessFully!");

			return "redirect:/jobseeker/viewDoc?message="+model.getAttribute("RESP");

		} catch (IOException e) {
			// Handle exceptions related to file operations or HTTP requests

			
			return null;
		}

	}
	
	
	@GetMapping("/deleteDocuments/{docId}")
	public String deletevacancy(@PathVariable("docId") int docId,Model model) {
	
		Map<String, Integer> pathVar = new HashMap<>();
		pathVar.put("docId", docId);
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.delete("http://3.108.237.172:8080/HireMeNow/api/v1/deleteDocuments/{docId}",pathVar);
		//ModelAndView view = new ModelAndView("viewProducts");
		//return view;
		model.addAttribute("RESP","Documents Deleted SuccessFully!");

		return "redirect:/jobseeker/viewDoc?message="+model.getAttribute("RESP");
		
	}
}
