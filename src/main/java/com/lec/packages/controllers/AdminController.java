package com.lec.packages.controllers;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

	 @GetMapping("/Facility_add")
	    public String addFaciltyPage(@AuthenticationPrincipal UserDetails userDetails,Model model) {
		 String userId = userDetails.getUsername();
		 model.addAttribute("userId",userId);
		 
		 return "admin/Facility_add";
	 }
	 @PostMapping("/Facility_add")
	 public String addFaciltyPagePost(@Valid FacilityDTO facilityDTO
			 							, BindingResult bindingResult
			 							, RedirectAttributes redirectAttributes
			 							,Model model
			 							,@AuthenticationPrincipal UserDetails userDetails) {
		 
		 if(bindingResult.hasErrors()) {
			 log.info("입력된 정보에 에러가 있습니다..");
			 redirectAttributes.addFlashAttribute("errors",bindingResult.getAllErrors());
			 
			 
			 String userId = userDetails.getUsername();
			 model.addAttribute("userId",userId);
			 
			 
			 return "redirect:/admin/main";
		 }
		 
		 log.info("register..........."+facilityDTO);
		 
		 String facilityCode = facilityService.register(facilityDTO);
		 redirectAttributes.addFlashAttribute("result",facilityCode);
		 
		 return "redirect:/admin/Facility_list";
	 }
	 
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
	 
	 @GetMapping("/Facility_edit")
	 public String EditFaciltyPage() {
		 return "admin/Facility_edit";
	 }
	 
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