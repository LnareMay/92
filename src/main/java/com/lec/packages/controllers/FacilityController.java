package com.lec.packages.controllers;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import com.lec.packages.dto.FacilityDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.service.FacilityService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/facility")
@RequiredArgsConstructor
public class FacilityController {
	
    private final FacilityService facilityService;
	 
	    @GetMapping("/facility_main")
	    public String ListFaciltyPage(HttpServletRequest request,PageRequestDTO pageRequestDTO , Model model) {
	    	 
	    	 String requestURI = request.getRequestURI();  	 
 
			 PageResponseDTO<FacilityDTO> responseDTO = facilityService.list(pageRequestDTO);
			
			 log.info("............................."+responseDTO);
			 
			
			 model.addAttribute("currentURI", requestURI); // requestURI를 모델에 추가
			 model.addAttribute("facilities",responseDTO.getDtoList());	
			 model.addAttribute("totalPages", responseDTO.getTotal());  //총페이지
			 model.addAttribute("pageNumber", pageRequestDTO.getPage()); // 현재 페이지 번호
			 model.addAttribute("pageSize", pageRequestDTO.getSize()); // 한 페이지당 항목 수
			 
			 return "facility/facility_main";		 
		 }

}
	