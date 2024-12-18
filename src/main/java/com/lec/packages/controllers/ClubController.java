package com.lec.packages.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lec.packages.dto.ClubDTO;
import com.lec.packages.service.ClubService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/club")
@RequiredArgsConstructor
public class ClubController {
	
	@Autowired
	private final ClubService clubService;
    
    @GetMapping("/list")
    public void getClubList(){
        
    }
	
	@GetMapping("/club_create")
	public String clubCreateGet() {
		return "club/club_create"; 
	}
	
	@PostMapping("/club_create")
	public String clubCreatePost(@Valid ClubDTO clubDTO
			, RedirectAttributes redirectAttributes) {
		
		log.info("Create.." + clubDTO);		
		String code = clubService.create(clubDTO);
		redirectAttributes.addFlashAttribute("result", code);		
		return "redirect:/club/club_create"; 
	}
	
	@GetMapping("/club_detail")
	public String clubDetail() {
		return "club/club_detail"; 
	}
	
	@GetMapping("/club_board")
	public String clubBoard() {
		return "club/club_board"; 
	}
	
	@GetMapping("/club_calendar")
	public String clubCalendar() {
		return "club/club_calendar"; 
	}
}
