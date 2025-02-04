package com.lec.packages.controllers;

import com.lec.packages.domain.Member_Planner;
import com.lec.packages.domain.Reservation;
import com.lec.packages.domain.Reservation_Member_List;
import com.lec.packages.service.ClubService;
import com.lec.packages.service.MemberPlannerService;
import com.lec.packages.service.ReservationService;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/planner")
@RequiredArgsConstructor
public class MemberPlannerController {

	private final MemberPlannerService memberPlannerService;
	private final ClubService clubService;
	private final ReservationService reservationService;

	@GetMapping("/list")
	public ResponseEntity<List<Map<String, Object>>> getPlanners(@RequestParam("memId") String memId) {
	    List<Map<String, Object>> formattedPlanners = new ArrayList<>();

	 // 1️⃣ 일반 일정(Member_Planner) 가져오기 (planIsclub == false 만 필터링)
	    List<Member_Planner> planners = memberPlannerService.getAllPlanners(memId)
	        .stream()
	        .filter(planner -> !planner.isPlanIsclub()) // ✅ planIsclub이 false인 경우만 가져옴
	        .collect(Collectors.toList());

	    for (Member_Planner planner : planners) {
	        Map<String, Object> map = new HashMap<>();
	        map.put("id", planner.getPlanNo());
	        map.put("title", planner.getPlanName());
	        map.put("start", planner.getPlanDate().toString());
	        map.put("planText", planner.getPlanText());
	        map.put("planIschk", planner.isPlanIschk());
	        map.put("planIsclub", planner.isPlanIsclub());
	        formattedPlanners.add(map);
	    }


	    // 2️⃣ 클럽 일정(Reservation_Member_List) 가져오기
	    List<Reservation_Member_List> clubReservations = memberPlannerService.getClubReservations(memId);

	    for (Reservation_Member_List res : clubReservations) {
	        // ✅ 이미 등록된 일정인지 확인
	        boolean exists = planners.stream()
	            .anyMatch(planner -> planner.getReservationCode() != null &&
	                                 planner.getReservationCode().equals(res.getReservationCode()));

	        String clubName = clubService.getClubNameByCode(res.getClubCode());
            String facilityName = reservationService.getFacilityNameByCode(res.getReservationCode())
                    .map(Reservation::getFacilityName)
                    .orElse("시설 정보 없음"); 
            String startTime = reservationService.getFacilityNameByCode(res.getReservationCode())
                    .map(reservation -> reservation.getReservationStartTime().toString()) 
                    .orElse("시작 시간 없음");
            String endTime = reservationService.getFacilityNameByCode(res.getReservationCode())
                    .map(reservation -> reservation.getReservationEndTime().toString()) 
                    .orElse("종료 시간 없음");
            
	        if (!exists) { // ✅ 중복 등록 방지
	            

	            // ✅ DB에 클럽 일정 추가
	            Member_Planner newPlanner = Member_Planner.builder()
	                .memId(res.getMemId())
	                .planDate(res.getReservationDate())
	                .planName("[클럽] " + clubName)
	                .planText("장소 : " + facilityName + "\n시간 :" + startTime + "~" + endTime)
	                .planIschk(false)
	                .planIsclub(true)
	                .reservationCode(res.getReservationCode())
	                .deleteFlag(false)
	                .build();

	            memberPlannerService.savePlanner(newPlanner); // ✅ 일정 저장
	        }

	        Map<String, Object> map = new HashMap<>();
	        map.put("id", res.getReservationCode());
	        map.put("title", "[클럽] " + clubName);
	        map.put("start", res.getReservationDate().toString());
	        map.put("planText", "장소 : " + facilityName + "\n시간 :" + startTime + "~" + endTime);
	        map.put("planIschk", false);
	        map.put("planIsclub", true);
	        map.put("color", "#ff9800");

	        formattedPlanners.add(map);
	    }

	    return ResponseEntity.ok(formattedPlanners);
	}

	// 일정 추가
	@PostMapping("/add")
	public Member_Planner addPlanner(@RequestBody Member_Planner planner) {
		return memberPlannerService.savePlanner(planner);
	}

	// 특정 날짜 일정 조회
	@GetMapping("/date")
	public List<Member_Planner> getPlannerByDate(@RequestParam("memId") String memId,
			@RequestParam("planDate") Date planDate) {
		return memberPlannerService.getPlannerByDate(memId, planDate);
	}

	// 일정 삭제
	@DeleteMapping("/delete")
	public void deletePlanner(@RequestParam("planNo") int planNo) {
		memberPlannerService.deletePlanner(planNo);
	}

	// ✅ 일정 완료 상태 업데이트 API (토글 기능)
	@PutMapping("/updateIschk")
	public ResponseEntity<String> updatePlanIschk(@RequestBody Member_Planner planner) {
		Member_Planner existingPlanner = memberPlannerService.getPlannerById(planner.getPlanNo());

		if (existingPlanner != null) {
			existingPlanner.setPlanIschk(planner.isPlanIschk()); // ✅ true/false 값 반영
			memberPlannerService.savePlanner(existingPlanner);
			System.out.println("📌 일정 완료 상태 변경됨: " + planner.getPlanNo() + " → " + planner.isPlanIschk());
			return ResponseEntity.ok("✅ 일정 상태 변경 성공!");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("🚨 일정이 존재하지 않습니다.");
		}
	}

	// 특정 일정 조회 API (상세보기)
	@GetMapping("/detail")
	public Member_Planner getPlannerDetail(@RequestParam int planNo) {
		return memberPlannerService.getPlannerById(planNo);
	}

	// 클럽 일정 가져오기
//    @PostMapping("/convertReservations")
//    public ResponseEntity<String> convertReservations(@RequestParam String memId) {
//        memberPlannerService.saveReservationsAsPlans(memId);
//        return ResponseEntity.ok("예약 데이터가 일정으로 변환되었습니다.");
//    }
}
