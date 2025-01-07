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
import com.lec.packages.dto.ClubMemberDTO;
import com.lec.packages.dto.MemberSecurityDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.repository.ClubMemberRepository;
import com.lec.packages.service.ClubService;
import com.lec.packages.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2
@Controller
@RequestMapping("/club")
@RequiredArgsConstructor
public class ClubController {
	
    @Value("${com.lec.upload.path}")
    private String uploadPath;
	
	@Autowired
	private final ClubService clubService;

	private final ClubMemberRepository clubMemberRepository;
	
	@GetMapping("/club_create")
	public String clubCreateGet(HttpServletRequest request, Model model) {
		String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI);
		return "club/club_create"; 
	}
	
	@GetMapping({"/club_detail", "/club_modify"})
	public void clubDetail(@RequestParam("clubCode") String clubCode
			, PageRequestDTO pageRequestDTO
			, HttpServletRequest request, Model model) {
		String requestURI = request.getRequestURI();
		model.addAttribute("currentURI", requestURI);
				
		ClubDTO clubDTO = clubService.detail(clubCode);		
		if(clubDTO.getClubTheme() != null && !clubDTO.getClubTheme().isEmpty()) {
			clubDTO.setClubTheme(clubDTO.getClubTheme());
		}
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MemberSecurityDTO principal = (MemberSecurityDTO) authentication.getPrincipal();
		
		PageResponseDTO<ClubMemberDTO> clubMemberdto = clubService.clubMemberList(clubCode, pageRequestDTO);
        model.addAttribute("clubMemberdto", clubMemberdto);
        
		int memberCount = clubService.membercount(clubCode);
		model.addAttribute("memberCount", memberCount);
		
		log.info(clubDTO);
		
		model.addAttribute("principal", principal);
        model.addAttribute("clubdto", clubDTO);
	}
		
	@PostMapping("/club_remove")
	public String clubRemove(@RequestParam(value = "clubCode", required = false) String clubCode
			, HttpServletRequest request
			, RedirectAttributes redirectAttributes) {
		clubService.remove(clubCode);
		
		redirectAttributes.addFlashAttribute("success", "클럽 삭제 성공");
		return "redirect:/";
	}
	
	@PostMapping("/club_join")
	public String clubJoin(@RequestParam(value = "clubCode", required = false) String clubCode
			, Authentication authentication
			, HttpServletRequest request
			, RedirectAttributes redirectAttributes) {
		String memId = authentication.getName();
		
		clubService.join(memId, clubCode);
		
		return "redirect:/club/club_detail?clubCode=" + clubCode;
	}
	
	@GetMapping("/club_member")
	public String clubMember(@RequestParam("clubCode") String clubCode
			, PageRequestDTO pageRequestDTO
			, HttpServletRequest request, Model model) {
		String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI);

        ClubDTO clubDTO = clubService.detail(clubCode);
        model.addAttribute("clubdto", clubDTO);

        PageResponseDTO<ClubMemberDTO> responseDTO = clubService.clubMemberList(clubCode, pageRequestDTO);
        model.addAttribute("responseDTO", responseDTO);
        
        int memberCount = clubMemberRepository.countByClubCode(clubCode).orElse(0);
        model.addAttribute("memberCount", memberCount);
        
		return "club/club_member"; 
	}

	@GetMapping("/club_board")
	public String clubBoard(@RequestParam("clubCode") String clubCode, PageRequestDTO pageRequestDTO, @RequestParam(value = "type", required = false) String type
			, HttpServletRequest request, Model model) {
		String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI);
        
		log.info("do clubBoardList");
		ClubDTO clubDTO = clubService.detail(clubCode);
        model.addAttribute("clubdto", clubDTO);

		pageRequestDTO.setType(type);

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
