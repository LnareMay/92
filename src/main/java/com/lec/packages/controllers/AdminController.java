package com.lec.packages.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	@GetMapping("/admin_main")
	public String facilityMain() {
		return "admin/admin_main"; 
	    }
	
	
}