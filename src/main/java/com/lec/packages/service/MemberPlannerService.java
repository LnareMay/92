package com.lec.packages.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lec.packages.domain.Member_Planner;
import com.lec.packages.domain.Reservation;
import com.lec.packages.domain.Reservation_Member_List;
import com.lec.packages.dto.ReservationDTO;
import com.lec.packages.dto.TransferHistoryDTO;
import com.lec.packages.repository.ChargeHistoryRepository;
import com.lec.packages.repository.ClubReservationMemberRepository;
import com.lec.packages.repository.ExerciseRepository;
import com.lec.packages.repository.MemberPlannerRepository;
import com.lec.packages.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberPlannerService {

	private final MemberPlannerRepository memberPlannerRepository;
	private final ClubReservationMemberRepository clubReservationMemberRepository;
	private final FacilityService facilityService;

	// 일정 저장
	public Member_Planner savePlanner(Member_Planner planner) {
		return memberPlannerRepository.save(planner);
	}

	// 특정 회원의 특정 날짜 일정 조회
	public List<Member_Planner> getPlannerByDate(String memId, Date planDate) {
		return memberPlannerRepository.findByMemIdAndPlanDate(memId, planDate);
	}

	// 특정 회원의 모든 일정 조회
	public List<Member_Planner> findNonClubPlannersByMemId(String memId) {
		return memberPlannerRepository.findByMemId(memId);
	}

	// 일정 삭제
	public boolean deletePlanner(Integer planNo, TransferHistoryDTO transferHistoryDTO, ReservationDTO reservationDTO,
			@AuthenticationPrincipal UserDetails userDetails) {
		try {
// 1. 일정 조회
			Member_Planner member_planner = memberPlannerRepository.findById(planNo)
					.orElseThrow(() -> new IllegalArgumentException("해당 일정을 찾을 수 없습니다."));

// 2. 예약 코드 여부 확인
			if (member_planner.getReservationCode() != null) {
// (클럽시설 또는 시설) 예약 일정인 경우, 일정 비활성화 처리
				String memId = userDetails.getUsername();
				facilityService.cancelBooking(memId, transferHistoryDTO, reservationDTO);
				return true;
			} else {
// 개인 일정인 경우, 예약 취소
				try {
					member_planner.setDeleteFlag(true);
					memberPlannerRepository.save(member_planner);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("🚨 개인 일정 예약 취소 중 오류 발생: " + e.getMessage());
					return false;
				}
			}
		} catch (Exception e) {
			System.err.println("🚨 일정 삭제 중 오류 발생: " + e.getMessage());
			return false;
		}
	}

	public Member_Planner getPlannerById(Integer planNo) {
		return memberPlannerRepository.findByPlanNoAndDeleteFlagFalse(planNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다. planNo: " + planNo));
	}

	public List<Reservation_Member_List> getClubReservations(String memId) {
		return clubReservationMemberRepository.findClubReservationsWithDetails(memId);
	}

	public List<Reservation> getMemberReservations(String memId) {
		return clubReservationMemberRepository.findMemberReservationsWithDetails(memId);
	}

}
