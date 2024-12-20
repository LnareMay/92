package com.lec.packages.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/facility")
@RequiredArgsConstructor
public class FacilityController {
//	@GetMapping("/facility_main")
//	public String facilityMain(HttpServletRequest request, Model model) {
//		String requestURI = request.getRequestURI();
//		model.addAttribute("currentURI", requestURI); // requestURI를 모델에 추가
//		return "facility/facility_main";
//	}

}