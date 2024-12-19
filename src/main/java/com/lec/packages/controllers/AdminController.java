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
	
	 @GetMapping("/main")
	 public String adminMainPage() {
	     return "admin/Admin_Main"; 
	 }
	
	 @GetMapping("/Admin_edit")
	 public String editAdmin() {
		 return "admin/Admin_edit";
	 }

	 @GetMapping("/Facility_add")
	    public String addFaciltyPage() {
		 return "admin/Facility_add";
	 }
	 
	 @GetMapping("/Facility_detail")
	 public String DetailFaciltyPage() {
		 return "admin/Facility_detail";
	 }
	 
	 @GetMapping("/Facility_edit")
	 public String EditFaciltyPage() {
		 return "admin/Facility_edit";
	 }
	 
	 @GetMapping("/Facility_list")
	 public String ListFaciltyPage() {
		 return "admin/Facility_list";
	 }
	 
	 @GetMapping("/calendar")
	 public String Calendar() {
		 return "admin/calendar";
	 }
	
}