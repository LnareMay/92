package com.lec.packages.controllers;


import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.lec.packages.dto.FacilityDTO;
import com.lec.packages.dto.MemberJoinDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.dto.ReservationDTO;
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
	 
	    @GetMapping("/main")
	    public String ListFaciltyPage(HttpServletRequest request,PageRequestDTO pageRequestDTO , Model model) {
	    	 
	    	 String requestURI = request.getRequestURI();  	 
 
			 PageResponseDTO<FacilityDTO> responseDTO = facilityService.list(pageRequestDTO);
			
			 log.info("............................."+responseDTO);
			 
			
			 model.addAttribute("currentURI", requestURI); // requestURI를 모델에 추가
			 model.addAttribute("facilities",responseDTO.getDtoList());	
			 model.addAttribute("totalPages", responseDTO.getTotal());  //총페이지
			 model.addAttribute("pageNumber", pageRequestDTO.getPage()); // 현재 페이지 번호
			 model.addAttribute("pageSize", pageRequestDTO.getSize()); // 한 페이지당 항목 수
			 
			 return "facility/facility_main";		 
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
	    
	    @PostMapping("/submit-booking")
		public String facilityBookByMemberPost(ReservationDTO reservationDTO, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		    log.info("시설예약 POST방식.....");
		    log.info(reservationDTO);

	        facilityService.bookByMember(reservationDTO);
	        redirectAttributes.addFlashAttribute("result", "시설예약 성공");
		    

	        String redirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/member/reservation")
                    .toUriString();

	        return "redirect:" + redirectUrl;
		    
		}

}
	