package com.lec.packages.controllers;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lec.packages.dto.ClubDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.service.ClubService;

@Controller
public class MainController {
	
	@Autowired
	private ClubService clubService;


    @GetMapping("/")
    public String mainClub(PageRequestDTO pageRequestDTO, HttpServletRequest request, Model model
    					 , @RequestParam(value = "theme", required = false) String clubTheme) {
    	
        String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI); // requestURI를 모델에 추가
        
        if(pageRequestDTO.getPage() < 1) {
        	pageRequestDTO.setPage(1);
        }
        
    	
    	if (clubTheme == null || clubTheme.isEmpty()) {
    		clubTheme = "ALL";
    	} 
    	
    	PageResponseDTO<ClubDTO> responseDTO = "ALL".equals(clubTheme)
    			? clubService.list(pageRequestDTO)
    			: clubService.listByTheme(pageRequestDTO, clubTheme);

        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("clubs", responseDTO.getDtoList());
        model.addAttribute("theme", clubTheme);

        return "index";
    }   

    
    @GetMapping("/facility/facility_main")
    public String mainFacility(HttpServletRequest request, Model model) {
        String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI); // requestURI를 모델에 추가
        return "/facility/facility_main"; // Thymeleaf 템플릿 파일 (facility_main.html)
    }
}
