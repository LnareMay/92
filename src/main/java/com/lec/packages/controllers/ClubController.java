package com.lec.packages.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.multipart.MultipartFile;
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
	
	@PostMapping("/club_create")
	public String clubCreatePost(@Valid ClubDTO clubDTO
			, BindingResult bindingResult
			, RedirectAttributes redirectAttributes
			, @RequestParam("file") MultipartFile file
			, HttpServletRequest request, Model model) {
		
		String requestURI = request.getRequestURI();
		model.addAttribute("currentURI", requestURI);
		
		/*
		 * if(!file.isEmpty()) { String storedFileName =
		 * UploadResultDTO.storeFile(file); clubDTO.setClubImage1(storedFileName); }
		 */	

		log.info("Create.." + clubDTO);
		clubService.create(clubDTO);
		
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

	
	@GetMapping("/club_board")
	public String clubBoard(@RequestParam("clubCode") String clubCode, PageRequestDTO pageRequestDTO
			, HttpServletRequest request, Model model) {
		String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI);
        
		log.info("do clubBoardList");
		ClubDTO clubDTO = clubService.detail(clubCode);
        model.addAttribute("clubdto", clubDTO);

		PageResponseDTO<ClubBoardAllListDTO> boardDTO = clubService.listWithAll(pageRequestDTO, clubCode);
        log.info(boardDTO);
		model.addAttribute("responseDTO", boardDTO);

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
	@GetMapping({"/club_board_read"})
	public String clubBoardRead(HttpServletRequest request, Model model, @RequestParam("boardNo") int boardNo, @RequestParam("clubCode") String clubCode) {
		String requestURI = request.getRequestURI();
		log.info("do clubBoardRead");
		ClubBoardDTO clubBoardDTO = clubService.readOne(boardNo, clubCode);
		model.addAttribute("currentURI", requestURI);
		model.addAttribute("dto", clubBoardDTO);
		log.info(clubBoardDTO);

		return "club/club_board_readOne";
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping({"/club_board_modify"})
	public String callClubBoardModify(HttpServletRequest request, Model model, @RequestParam("boardNo") int boardNo, @RequestParam("clubCode") String clubCode) {
		String requestURI = request.getRequestURI();
		log.info("do clubBoardRead");
		ClubBoardDTO clubBoardDTO = clubService.readOne(boardNo, clubCode);
		model.addAttribute("currentURI", requestURI);
		model.addAttribute("dto", clubBoardDTO);
		log.info(clubBoardDTO);

		return "club/club_board_modify";
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/modify_Board")
	public String clubBoardModify(HttpServletRequest request, Model model, @RequestParam(name = "memId", defaultValue = "") String memId,
								@RequestParam(name = "boardNo", defaultValue = "") String boardNo,
								@Valid ClubBoardDTO clubBoardDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		log.info("do boardModify");
		clubBoardDTO.setBOARD_NO(Integer.parseInt(boardNo));
		ClubBoardDTO resultClubBoardDTO = clubService.modifyClubBoard(clubBoardDTO);

		model.addAttribute("currentURI", request.getRequestURI());
		model.addAttribute("dto", resultClubBoardDTO);

		return "club/club_board_readOne";
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/board_remove")
	public String clubBoardRemove(HttpServletRequest request, Model model, @RequestParam(name = "boardNo", defaultValue = "") String boardNo,
								@Valid ClubBoardDTO clubBoardDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		log.info("do boardRemove");
		clubBoardDTO.setBOARD_NO(Integer.parseInt(boardNo));
		String clubCode = clubService.removeClubBoard(clubBoardDTO);

		return "redirect:/club/club_board?clubCode="+clubCode;
	}
}
