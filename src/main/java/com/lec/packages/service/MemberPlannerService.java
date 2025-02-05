package com.lec.packages.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lec.packages.domain.Member_Planner;
import com.lec.packages.domain.Reservation;
import com.lec.packages.domain.Reservation_Member_List;
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
	public boolean deletePlanner(Integer planNo) {
	    try {
	        Member_Planner member_planner = memberPlannerRepository.findById(planNo)
	                .orElseThrow(() -> new IllegalArgumentException("해당 일정을 찾을 수 없습니다."));

	        member_planner.setDeleteFlag(true);
	        memberPlannerRepository.save(member_planner);
	        return true; // ✅ 삭제 성공 시 true 반환
	    } catch (Exception e) {
	        System.err.println("🚨 일정 삭제 중 오류 발생: " + e.getMessage());
	        return false; // ❌ 삭제 실패 시 false 반환
	    }
	}


	public Member_Planner getPlannerById(Integer planNo) {
		return memberPlannerRepository.findByPlanNoAndDeleteFlagFalse(planNo)
				.orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다. planNo: " + planNo));
	}

	public List<Reservation_Member_List> getClubReservations(String memId) {
		return clubReservationMemberRepository.findClubReservationsWithDetails(memId);
	}
	
	

	

}
