package com.lec.packages.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lec.packages.dto.ClubBoardAllListDTO;
import com.lec.packages.dto.ClubBoardDTO;
import com.lec.packages.dto.ClubDTO;
import com.lec.packages.dto.MemberSecurityDTO;
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
	
	@GetMapping({"/club_detail", "/club_modify"})
	public void clubDetail(@RequestParam("clubCode") String clubCode
			, HttpServletRequest request, Model model) {
		String requestURI = request.getRequestURI();
		model.addAttribute("currentURI", requestURI);
				
		ClubDTO clubDTO = clubService.detail(clubCode);		
		if(clubDTO.getClubTheme() != null && !clubDTO.getClubTheme().isEmpty()) {
			clubDTO.setClubTheme(clubDTO.getClubTheme());
		}
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MemberSecurityDTO principal = (MemberSecurityDTO) authentication.getPrincipal();
		
		model.addAttribute("principal", principal);
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
	
	@PostMapping("/club_remove")
	public String clubRemove(@RequestParam(value = "clubCode", required = false) String clubCode
			, HttpServletRequest request
			, RedirectAttributes redirectAttributes) {
		clubService.remove(clubCode);
		
		redirectAttributes.addFlashAttribute("success", "클럽 삭제 성공");
		return "redirect:/";
	}
	

	
	@GetMapping("/club_board")
	public String clubBoard(@RequestParam("clubCode") String clubCode, PageRequestDTO pageRequestDTO
			, HttpServletRequest request, Model model) {
		String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI);
        
		ClubDTO clubDTO = clubService.detail(clubCode);
        model.addAttribute("clubdto", clubDTO);

		PageResponseDTO<ClubBoardAllListDTO> boardDTO = clubService.listWithAll(pageRequestDTO);
        
		return "club/club_board"; 
	}


	@PostMapping("/board_register")
	public String clubBoardPost(@RequestParam("CLUB_CODE") String clubCode
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
		return "redirect:/club/club_board?clubCode="+clubCode;
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

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/club_board_read")
	public String clubBoardRead(HttpServletRequest request, Model model, @RequestParam("boardNo") int boardNo, @RequestParam("clubCode") String clubCode) {
		String requestURI = request.getRequestURI();
		log.info("do clubBoardRead");
		ClubBoardDTO clubBoardDTO = clubService.readOne(boardNo, clubCode);
		model.addAttribute("currentURI", requestURI);
		model.addAttribute("dto", clubBoardDTO);
		log.info(clubBoardDTO);

		return "club/club_board_readOne";
	}
}
