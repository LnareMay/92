package com.lec.packages.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lec.packages.dto.FacilityDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;

import com.lec.packages.service.FacilityService;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	
	private final FacilityService facilityService;

	
	 @GetMapping("/main")
	 public String adminMainPage(@AuthenticationPrincipal UserDetails userDetails,PageRequestDTO pageRequestDTO, Model model) {
		 
		
		 String userId = userDetails.getUsername();
		 PageResponseDTO<FacilityDTO> responseDTO = facilityService.listByUser(userId,pageRequestDTO);

		 model.addAttribute("userId",userId);
		 model.addAttribute("facilities",responseDTO.getDtoList());
		 
		 
	     return "admin/Admin_Main"; 
	 }
	
	 @GetMapping("/Admin_edit")
	 public String editAdmin(@AuthenticationPrincipal UserDetails userDetails,Model model) {
		 
		 String userId = userDetails.getUsername();
		 model.addAttribute("userId",userId);
		 return "admin/Admin_edit";
	 }
	 //Get 시설 등록
	 @GetMapping("/Facility_add")
	 public String addFacilityPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
	     String userId = userDetails.getUsername();
	     model.addAttribute("userId", userId);
	     model.addAttribute("facilityDTO", new FacilityDTO()); // 빈 DTO 객체 전달
	     return "admin/Facility_add"; // 입력 페이지로 이동
	 }
 
	 //시설 수정하기 Get
	 @GetMapping("/Facility_edit/{facilityCode}")
	 public String EditFaciltyPage( @PathVariable("facilityCode") String facilityCode, 
									 Model model,
									 @AuthenticationPrincipal UserDetails userDetails) {
		 
		 String userId = userDetails.getUsername();
		 
		 //시설 정보를 가져오기 위해 서비스 호출
		 FacilityDTO facilityDTO = facilityService.getFacilityByCode(facilityCode);
		 
		 //모델에 로그인 정보를 추가하여 뷰로 전달
		 model.addAttribute("userId",userId);
		 //모델에 시설 정보를 추가하여 뷰로 전달
		 model.addAttribute("facility",facilityDTO);
		 
		 return "admin/Facility_edit";
	 }
	 
	 
	 //시설 수정하기 Post
	 @PostMapping("/Facility_edit/{facilityCode}")
	 public String EditFaciltyPagePost(@Valid FacilityDTO facilityDTO
									 	,@PathVariable("facilityCode") String facilityCode
										,BindingResult bindingResult
										,RedirectAttributes redirectAttributes
										,Model model
										,@AuthenticationPrincipal UserDetails userDetails
										,@RequestParam("exerciseCode") String exerciseCode) {
		 
		 // NULL로 변환하여 DB에 저장할 수 있도록 함.
		 if (exerciseCode == null || exerciseCode.isEmpty()) {
		        exerciseCode = null;
		    }

	    if (bindingResult.hasErrors()) {
	  
	    	log.info("입력된 정보에 에러가 있습니다: {}", bindingResult.getAllErrors());
	       
	        String userId = userDetails.getUsername();
	        
	        // 에러 메시지와 사용자 입력 데이터를 모델에 추가
	        model.addAttribute("facilityDTO", facilityDTO);
    	    model.addAttribute("userId", userId);
    	    
	        model.addAttribute("errors", bindingResult.getFieldErrors());


	        // 입력 페이지로 다시 이동
	        return "admin/Facility_add";
	    }

	    log.info("등록 요청: {}", facilityDTO);

	    // 시설 등록 및 결과 처리
	    facilityService.modify(facilityDTO);
	    redirectAttributes.addFlashAttribute("result", "시설이 성공적으로 등록되었습니다");
		 
		 return "admin/Facility_list";
	 }
	 
	 //시설 상세보기
	 @GetMapping("/Facility_detail/{facilityCode}")
	 public String DetailFaciltyPage(@PathVariable("facilityCode") String facilityCode, Model model,@AuthenticationPrincipal UserDetails userDetails) {
		 
		 String userId = userDetails.getUsername();
		 
		 //시설 정보를 가져오기 위해 서비스 호출
		 FacilityDTO facilityDTO = facilityService.getFacilityByCode(facilityCode);
		
		 //모델에 로그인 정보를 추가하여 뷰로 전달
		 model.addAttribute("userId",userId);
		 //모델에 시설 정보를 추가하여 뷰로 전달
		 model.addAttribute("facility",facilityDTO);
 
		 return "admin/Facility_detail";
	 }
	 

	 
	 //시설 리스트 보기
	 @GetMapping("/Facility_list")
	 public String ListFaciltyPage(PageRequestDTO pageRequestDTO , Model model,@AuthenticationPrincipal UserDetails userDetails) {
		 
		 String userId = userDetails.getUsername();

		 PageResponseDTO<FacilityDTO> responseDTO = facilityService.listByUser(userId,pageRequestDTO);
		 log.info("............................."+responseDTO);
		 
		 model.addAttribute("userId",userId);	
		
		 model.addAttribute("facilities",responseDTO.getDtoList());
		 model.addAttribute("totalPages", responseDTO.getTotal());
		 model.addAttribute("pageNumber", pageRequestDTO.getPage()); // 현재 페이지 번호
		 model.addAttribute("pageSize", pageRequestDTO.getSize()); // 한 페이지당 항목 수
		 
		 return "admin/Facility_list";		 
	 }
	 
	 @GetMapping("/calendar")
	 public String Calendar(Model model,@AuthenticationPrincipal UserDetails userDetails) {
		 
		 String userId = userDetails.getUsername();
		 model.addAttribute("userId",userId);	
		 
		 return "admin/calendar";
	 }
	 
	
	
}