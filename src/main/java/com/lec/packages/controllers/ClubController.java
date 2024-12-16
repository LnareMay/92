package com.lec.packages.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/club")
@RequiredArgsConstructor
public class ClubController {
    
    @GetMapping("/list")
    public void getClubList(){
        
    }
    
	@GetMapping("/club_main")
	public String clubMain() {
		return "club/club_main"; 
	}
	
	@GetMapping("/club_create")
	public String clubCreate() {
		return "club/club_create"; 
	}
	
	@GetMapping("/club_detail")
	public String clubDetail() {
		return "club/club_detail"; 
	}
}
