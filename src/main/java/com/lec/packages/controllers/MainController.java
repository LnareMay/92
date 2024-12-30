package com.lec.packages.controllers;

import jakarta.servlet.http.HttpServletRequest;

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
        
    	PageResponseDTO<ClubDTO> responseDTO = clubService.list(pageRequestDTO);
        model.addAttribute("responseDTO", responseDTO);
        
        // 테마가 전달되었을 경우 필터, 그렇지 않으면 전체 클럽 목록 반환
        List<ClubDTO> clubs = (clubTheme != null) ? clubService.ListByTheme(clubTheme) : clubService.getAllClubs();
        
        model.addAttribute("clubs", clubs);
        model.addAttribute("theme", clubTheme == null ? "" : clubTheme);

        return "index";
    }   

    
    @GetMapping("/facility/facility_main")
    public String mainFacility(HttpServletRequest request, Model model) {
        String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI); // requestURI를 모델에 추가
        return "/facility/facility_main"; // Thymeleaf 템플릿 파일 (facility_main.html)
    }
}
