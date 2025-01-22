package com.lec.packages.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lec.packages.domain.Facility;
import com.lec.packages.domain.Member;
import com.lec.packages.domain.Reservation;
import com.lec.packages.dto.FacilityDTO;
import com.lec.packages.dto.MemberSecurityDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.dto.ReservationDTO;
import com.lec.packages.repository.FacilityRepository;
import com.lec.packages.repository.MemberRepository;
import com.lec.packages.repository.ReservationRepository;
import com.lec.packages.service.FacilityService;
import com.lec.packages.service.ReservationService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final FacilityService facilityService;
	private final FacilityRepository facilityRepository;
	private final ReservationService reservationService;
	private final ReservationRepository reservationRepository;
	private final MemberRepository memberRepository;

	@GetMapping("/main")
	public String adminMainPage(@AuthenticationPrincipal UserDetails userDetails, PageRequestDTO pageRequestDTO,
			Model model) {

		String userId = userDetails.getUsername();

		PageResponseDTO<FacilityDTO> responseFacilityDTO = facilityService.listByUser(userId, pageRequestDTO);
		PageResponseDTO<ReservationDTO> responseReservationDTO = reservationService.getAllReservationsForUser(userId,
				pageRequestDTO);

		model.addAttribute("userId", userId);
		model.addAttribute("facilities", responseFacilityDTO.getDtoList());
		model.addAttribute("reservations", responseReservationDTO.getDtoList());

		return "admin/Admin_Main";
	}

	@GetMapping("/Admin_edit")
	public String editAdmin(@AuthenticationPrincipal UserDetails userDetails, Model model) {

		String userId = userDetails.getUsername();
		model.addAttribute("userId", userId);
		return "admin/Admin_edit";
	}

	// Get 시설 등록
	@GetMapping("/Facility_add")
	public String addFacilityPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String userId = userDetails.getUsername();
		model.addAttribute("userId", userId);
		model.addAttribute("facilityDTO", new FacilityDTO()); // 빈 DTO 객체 전달
		return "admin/Facility_add"; // 입력 페이지로 이동
	}

	// 시설 수정하기 Get
	@GetMapping("/Facility_edit/{facilityCode}")
	public String EditFaciltyPage(@PathVariable("facilityCode") String facilityCode, Model model,
			@AuthenticationPrincipal UserDetails userDetails) {

		String userId = userDetails.getUsername();

		// 시설 정보를 가져오기 위해 서비스 호출
		FacilityDTO facilityDTO = facilityService.getFacilityByCode(facilityCode);

		// 모델에 로그인 정보를 추가하여 뷰로 전달
		model.addAttribute("userId", userId);
		// 모델에 시설 정보를 추가하여 뷰로 전달
		model.addAttribute("facility", facilityDTO);

		return "admin/Facility_edit";
	}

	// 시설 상세보기
	@GetMapping("/Facility_detail/{facilityCode}")
	public String DetailFaciltyPage(@PathVariable("facilityCode") String facilityCode, Model model,
			@AuthenticationPrincipal UserDetails userDetails) {

		String userId = userDetails.getUsername();

		// 시설 정보를 가져오기 위해 서비스 호출
		FacilityDTO facilityDTO = facilityService.getFacilityByCode(facilityCode);

		// 모델에 로그인 정보를 추가하여 뷰로 전달
		model.addAttribute("userId", userId);
		// 모델에 시설 정보를 추가하여 뷰로 전달
		model.addAttribute("facility", facilityDTO);

		return "admin/Facility_detail";
	}

	// 시설 리스트 보기
	@GetMapping("/Facility_list")
	public String ListFaciltyPage(PageRequestDTO pageRequestDTO, Model model,
			@AuthenticationPrincipal UserDetails userDetails) {

		String userId = userDetails.getUsername();

		PageResponseDTO<FacilityDTO> responseDTO = facilityService.listByUser(userId, pageRequestDTO);
		log.info("............................." + responseDTO);

		model.addAttribute("userId", userId);

		model.addAttribute("facilities", responseDTO.getDtoList());
		model.addAttribute("totalPages", responseDTO.getTotal());
		model.addAttribute("pageNumber", pageRequestDTO.getPage()); // 현재 페이지 번호
		model.addAttribute("pageSize", pageRequestDTO.getSize()); // 한 페이지당 항목 수

		return "admin/Facility_list";
	}

//	 @GetMapping("/Reservation_list")
//	 public String ListReservationPage(PageRequestDTO pageRequestDTO, Model model, 
//	     @AuthenticationPrincipal UserDetails userDetails) {
//	     

//	     String memId = userDetails.getUsername();
//	     
//	     // 로그인 한 유저의 시설 코드들 받아오기 
//	     List<FacilityDTO> facilityDTOs = facilityService.getFacilityCodeByUser(userDetails.getUsername());
//	     
//	     // 모든 시설의 예약 정보를 담을 리스트
//	     List<ReservationDTO> allReservations = new ArrayList<>();
//	     
//	     // 각 시설의 예약 정보 조회 --> 凸성능 쓰레기凸
//	     for (FacilityDTO facility : facilityDTOs) {
//	         PageResponseDTO<ReservationDTO> responseDTO = 
//	             reservationService.getReservationByFacilityCode(facility.getFacilityCode(), pageRequestDTO);
//	         allReservations.addAll(responseDTO.getDtoList());
//	     }
//	     
//	     // 페이징 처리를 위한 계산
//	     int totalItems = allReservations.size();
//	     int pageSize = pageRequestDTO.getSize();
//	     int totalPages = (int) Math.ceil((double) totalItems / pageSize);
//	     
//	     model.addAttribute("memId", memId);
//	     model.addAttribute("facilities", facilityDTOs);
//	     model.addAttribute("reservations", allReservations);
//	     model.addAttribute("totalPages", totalPages);
//	     model.addAttribute("pageNumber", pageRequestDTO.getPage());
//	     model.addAttribute("pageSize", pageRequestDTO.getSize());
//	     
//	     return "admin/Reservation_list";
//	 }

	@GetMapping("/Facility_delete/{facilityCode}")
	public String deleteFacility(@PathVariable("facilityCode") String facilityCode) {
		// 시설 정보 조회
		Optional<Facility> optionalFacility = facilityRepository.findByFacilityCode(facilityCode);

		if (optionalFacility.isPresent()) {
			Facility facility = optionalFacility.get();
			// deleteFlag를 0으로 설정
			facility.setDeleteFlag(true);
			facilityRepository.save(facility);
		}

		return "redirect:/admin/Facility_list";
	}

	// 예약 리스트
	@GetMapping("/Reservation_list")
	public String ListReservationPage(PageRequestDTO pageRequestDTO
									  , Model model
								      ,@AuthenticationPrincipal UserDetails userDetails) {

		String memId = userDetails.getUsername();

		PageResponseDTO<ReservationDTO> responseDTO = reservationService.getAllReservationsForUser(memId,
				pageRequestDTO);

		model.addAttribute("memId", memId);
		model.addAttribute("reservations", responseDTO.getDtoList());
		model.addAttribute("totalPages", responseDTO.getTotal());
		model.addAttribute("pageNumber", pageRequestDTO.getPage());
		model.addAttribute("pageSize", pageRequestDTO.getSize());

		return "admin/Reservation_list";
	}

	// 예약 상세보기
	@GetMapping("/Reservation_detail/{reservationCode}")
	public String DetailReservationPage(@PathVariable("reservationCode") String reservationCode, Model model,
			@AuthenticationPrincipal UserDetails userDetails) {

		String memId = userDetails.getUsername();

		// 예약 정보를 가져오기 위해 서비스 호출
		ReservationDTO reservationDTO = reservationService.getReservationByCode(reservationCode);

		// 예약코드로 멤버정보 가져오기
//		 MemberSecurityDTO memberDTO = reservationService.getMemberInfoByReservationCode(reservationCode);

		// 모델에 로그인 정보를 추가하여 뷰로 전달
		model.addAttribute("memId", memId);
		// 모델에 시설 정보를 추가하여 뷰로 전달
		model.addAttribute("reservation", reservationDTO);
//		 model.addAttribute("member",memberDTO);

		// Member 객체를 가져오는 로직 추가 [사용자정보]
		Optional<Member> memberOptional = memberRepository.findById(reservationDTO.getMemId());
		if (memberOptional.isPresent()) {
			model.addAttribute("member", memberOptional.get());
		}
		
		// Member 객체를 가져오는 로직 추가 [관리자정보]
		Optional<Member> managerOptional = memberRepository.findById(memId);
		if (managerOptional.isPresent()) {
			model.addAttribute("manager", managerOptional.get());
		}

		return "admin/Reservation_detail";
	}

	// 승인 거절
	@GetMapping("/Reservation_refuse/{reservationCode}")
	public String refuseReservation(@PathVariable("reservationCode") String reservationCode) {

		// 예약 정보를 가져오기 위해 서비스 호출
		ReservationDTO reservationDTO = reservationService.getReservationByCode(reservationCode);

		// DTO를 엔티티로 변환하고 상태 변경
		Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
				.orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

		// 예약 상태 변경
		reservation.setReservationProgress("예약취소");
		reservationRepository.save(reservation);

		return "redirect:/admin/Reservation_list";
	}

	// 예약 취소 리스트(확인용)
	@GetMapping("/Reservation_Refuselist")
	public String ListReservationRefusPage(PageRequestDTO pageRequestDTO, Model model,
			@AuthenticationPrincipal UserDetails userDetails) {

		String memId = userDetails.getUsername();

		PageResponseDTO<ReservationDTO> responseDTO = reservationService.getAllReservationsForUser(memId,
				pageRequestDTO);

		model.addAttribute("memId", memId);
		model.addAttribute("reservations", responseDTO.getDtoList());
		model.addAttribute("totalPages", responseDTO.getTotal());
		model.addAttribute("pageNumber", pageRequestDTO.getPage());
		model.addAttribute("pageSize", pageRequestDTO.getSize());

		return "admin/Reservation_refuselist";
	}

	// 예약 승인
	@GetMapping("/Reservation_confirm/{reservationCode}")
	public String confirmReservation(@PathVariable("reservationCode") String reservationCode) {

		// 예약 정보를 가져오기 위해 서비스 호출
		ReservationDTO reservationDTO = reservationService.getReservationByCode(reservationCode);

		// DTO를 엔티티로 변환하고 상태 변경
		Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
				.orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

		// 예약 상태 변경
		reservation.setReservationProgress("예약완료");
		reservationRepository.save(reservation);

		return "redirect:/admin/Reservation_list";
	}

	// 예약 승인 리스트(확인용)
	@GetMapping("/Reservation_Confirmlist")
	public String ListReservationConfirmPage(PageRequestDTO pageRequestDTO, Model model,
			@AuthenticationPrincipal UserDetails userDetails) {

		String memId = userDetails.getUsername();

		PageResponseDTO<ReservationDTO> responseDTO = reservationService.getAllReservationsForUser(memId,
				pageRequestDTO);

		model.addAttribute("memId", memId);
		model.addAttribute("reservations", responseDTO.getDtoList());
		model.addAttribute("totalPages", responseDTO.getTotal());
		model.addAttribute("pageNumber", pageRequestDTO.getPage());
		model.addAttribute("pageSize", pageRequestDTO.getSize());

		return "admin/Reservation_confirmlist";
	}

	@GetMapping("/calendar")
	public String Calendar(Model model, @AuthenticationPrincipal UserDetails userDetails) {

		String userId = userDetails.getUsername();
		model.addAttribute("userId", userId);

		return "admin/calendar";
	}

}