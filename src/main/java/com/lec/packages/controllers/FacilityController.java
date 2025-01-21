package com.lec.packages.controllers;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.lec.packages.domain.Member;
import com.lec.packages.domain.TransferHistory;
import com.lec.packages.dto.FacilityDTO;
import com.lec.packages.dto.MemberJoinDTO;
import com.lec.packages.dto.MemberSecurityDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.dto.ReservationDTO;
import com.lec.packages.dto.TransferHistoryDTO;
import com.lec.packages.repository.MemberRepository;
import com.lec.packages.security.CustomUserDetailsService;
import com.lec.packages.service.ClubService;
import com.lec.packages.service.FacilityService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/facility")
@RequiredArgsConstructor
public class FacilityController {
	
    private final FacilityService facilityService;
    private final ClubService clubService;
    private final CustomUserDetailsService customUserDetailsService;
    private final MemberRepository memberRepository;
	    
	    @GetMapping("/main")
	    public String ListFaciltyPage(HttpServletRequest request,PageRequestDTO pageRequestDTO 
	    		, Model model
	    		, @RequestParam(value = "facilityAddress", required = false, defaultValue = "ALL") String facilityAddress
                , @RequestParam(value = "exerciseCode", required = false, defaultValue = "ALL") String exerciseCode
                , @RequestParam(value = "facilityIsOnlyClub", required = false) Boolean facilityIsOnlyClub) {
	    	
	    	String currentURI = request.getRequestURI();  
	    	
	    	if ("ALL".equals(facilityAddress)) {
	    		facilityAddress = "";	  
	    	} if ("ALL".equals(exerciseCode)) {
	    		exerciseCode = "";	  
	    	} 
	    	
	    	PageResponseDTO<FacilityDTO> responseDTO = facilityService.listAllFacility(pageRequestDTO, facilityAddress, exerciseCode, facilityIsOnlyClub);
	
	    	log.info("............................."+responseDTO);
	    		    	
	    	 model.addAttribute("currentURI", currentURI); // requestURI를 모델에 추가
	    	 model.addAttribute("responseDTO",responseDTO);	
			 model.addAttribute("facilityAddress", facilityAddress); 
			 model.addAttribute("exerciseCode", exerciseCode); 
			 model.addAttribute("facilityIsOnlyClub", facilityIsOnlyClub); 
	    	
	    	return "facility/facility_main";		 
	    }
	    
	    // 시설 검색조회
	    @PostMapping("/main/search")
	    public String searchFacility(PageRequestDTO pageRequestDTO,
	                                 @RequestParam(value = "facilityAddress", required = false) String facilityAddress,
	                                 @RequestParam(value = "exerciseCode", required = false) String exerciseCode,
	                                 @RequestParam(value = "facilityIsOnlyClub", required = false) Boolean facilityIsOnlyClub, 
	                                 RedirectAttributes redirectAttributes) {
	    	
	        redirectAttributes.addAttribute("facilityAddress", facilityAddress);
	        redirectAttributes.addAttribute("exerciseCode", exerciseCode);
	        redirectAttributes.addAttribute("facilityIsOnlyClub", facilityIsOnlyClub);
	        
	        log.info("facilityAddress: {}", facilityAddress);
	        log.info("exerciseCode: {}", exerciseCode);
	        log.info("facilityIsOnlyClub: {}", facilityIsOnlyClub);

	        return "redirect:/facility/main";
	    }
	    
	    @GetMapping("/detail/{facilityCode}")
	    public String FaciltyDetailPage( @PathVariable("facilityCode") String facilityCode
	    								,HttpServletRequest request
	    								,PageRequestDTO pageRequestDTO 
	    								, Model model
	    								,@AuthenticationPrincipal UserDetails userDetails) {
	    	
	    	//시설 정보를 가져오기 위해 서비스 호출
			FacilityDTO facilityDTO = facilityService.getFacilityByCode(facilityCode);
			List<ReservationDTO> reservations = facilityService.getReservationsByFacilityCode(facilityCode);
			//클럽장 체크
			boolean isClubOwner = clubService.checkClubOwner(userDetails.getUsername());

			//log.info("FacilityDTO: {}", facilityDTO);
			String userId = userDetails.getUsername();
			//모델에 로그인 정보를 추가하여 뷰로 전달
			model.addAttribute("userId",userId);
			//클럽장 체크 결과 전달
			model.addAttribute("isClubOwner", isClubOwner);
			//모델에 시설 정보를 추가하여 뷰로 전달
			model.addAttribute("facility",facilityDTO);
			model.addAttribute("reservations", reservations);
			model.addAttribute("currentURI", request.getRequestURI()); // 현재 URI 추가
			
	    	return "facility/facility_detail";		 
	    }
	    
	    // 시설예약
	    @PostMapping("/submit-booking")
		public String facilityBookByMemberPost(TransferHistoryDTO transferHistoryDTO, ReservationDTO reservationDTO, @RequestParam("memMoney") BigDecimal memMoney, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		    log.info("시설예약 POST방식.....");
		    log.info(reservationDTO);

	        facilityService.bookByMember(transferHistoryDTO, reservationDTO, memMoney);
	        redirectAttributes.addFlashAttribute("result", "시설예약 성공");
		    

	        String redirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/member/reservation")
                    .toUriString();

	        return "redirect:" + redirectUrl;
		    
		}
	    
	   

	    // 시설예약 취소
	    @PostMapping("/cancel-booking")
		public String facilityCancelPost(TransferHistoryDTO transferHistoryDTO, ReservationDTO reservationDTO, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		    

	    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		    if (authentication != null && authentication.getPrincipal() instanceof MemberSecurityDTO) {
		        MemberSecurityDTO member = (MemberSecurityDTO) authentication.getPrincipal();
		        String memId = member.getMemId(); // Current logged-in user's ID
		    
		        facilityService.cancelBooking(memId,transferHistoryDTO, reservationDTO);
		    
		        // 업데이트된 사용자 정보 가져오기
		        Member updatedMember = memberRepository.findById(memId)
		                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		        UserDetails updatedUser = customUserDetailsService.loadUserByUsername(updatedMember.getMemId());

		        // 새 인증 정보 생성
		        Authentication newAuth = new UsernamePasswordAuthenticationToken(
		                updatedUser,
		                updatedUser.getPassword(),
		                updatedUser.getAuthorities()
		        );

		        // 보안 컨텍스트 갱신
		        SecurityContextHolder.getContext().setAuthentication(newAuth);
		        
			    };
		        String redirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
	                    .path("/member/reservation")
	                    .toUriString();

		        return "redirect:" + redirectUrl; 
			
		    
		    
		    
		    }
	        
		

}
	