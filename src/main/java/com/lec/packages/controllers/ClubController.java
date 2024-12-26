package com.lec.packages.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lec.packages.dto.ClubBoardDTO;
import com.lec.packages.dto.ClubDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.service.ClubService;

import jakarta.servlet.http.HttpServletRequest;
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
	
	@GetMapping("/club_create")
	public String clubCreateGet(HttpServletRequest request, Model model) {
		String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI);
		return "club/club_create"; 
	}
	
	@PostMapping("/club_create")
	public String clubCreatePost(@Valid ClubDTO clubDTO
			, BindingResult bindingResult
			, RedirectAttributes redirectAttributes, HttpServletRequest request, Model model) {
		
		String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI);
        
		log.info("Create.." + clubDTO);		
		String code = clubService.create(clubDTO);
		redirectAttributes.addFlashAttribute("result", code);		
		return "redirect:/"; 
	}
	
	@GetMapping({"/club_detail", "/club_modify"})
	public void clubDetail(@RequestParam("clubCode") String clubCode
			, HttpServletRequest request, Model model) {
		String requestURI = request.getRequestURI();
		model.addAttribute("currentURI", requestURI);
				
		ClubDTO clubDTO = clubService.detail(clubCode);		
		if(clubDTO.getClubTheme() != null && !clubDTO.getClubTheme().isEmpty()) {
			clubDTO.setClubTheme(clubDTO.getClubTheme());
		}
			
        model.addAttribute("clubdto", clubDTO);
	}
	
	@PostMapping("/club_modify")
	public String clubModify(@Valid ClubDTO clubDTO
			, HttpServletRequest request, Model model
			, PageRequestDTO pageRequestDTO
			, RedirectAttributes redirectAttributes) {
		String requestURI = request.getRequestURI();
		model.addAttribute("currentURI", requestURI);
		
		log.info("modify : " + clubDTO);
		clubService.modify(clubDTO);		
		
		redirectAttributes.addAttribute("clubCode", clubDTO.getClubCode());		
		return "redirect:/club/club_detail"; 
	}
	
	@GetMapping("/club_board")
	public String clubBoard(@RequestParam("clubCode") String clubCode
			, HttpServletRequest request, Model model) {
		String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI);
        
		ClubDTO clubDTO = clubService.detail(clubCode);
        model.addAttribute("clubdto", clubDTO);
        
		return "club/club_board"; 
	}

	@PostMapping("board_register")
	public String clubBoardPost(@RequestParam("clubCode") String clubCode
			,HttpServletRequest request, Model model
			,@Valid ClubBoardDTO clubBoardDTO
			, BindingResult bindingResult
			, RedirectAttributes redirectAttributes){
		String requestURI = request.getRequestURI();
		model.addAttribute("currentURI", requestURI);
		log.info("registerController");

		log.info(clubBoardDTO);
		int bno = clubService.registerClubBoard(clubBoardDTO);

		log.info(bno);
		return "redirect:/club/club_board";
	}
	
	
	@GetMapping("/club_calendar")
	public String clubCalendar(HttpServletRequest request, Model model) {
		String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI);
        
		return "club/club_calendar"; 
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/club_board_write")
	public String clubBoardWrite(HttpServletRequest request, Model model){
		String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI);
        
		return "club/club_board_write";
	}
}
