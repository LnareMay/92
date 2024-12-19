package com.lec.packages.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String mainClub(HttpServletRequest request, Model model) {
        String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI); // requestURI를 모델에 추가
        return "index"; // Thymeleaf 템플릿 파일 (index.html)
    }
    
    
    @GetMapping("/facility/facility_main")
    public String mainFacility(HttpServletRequest request, Model model) {
        String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI); // requestURI를 모델에 추가
        return "/facility/facility_main"; // Thymeleaf 템플릿 파일 (facility_main.html)
    }
}
