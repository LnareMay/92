package com.lec.packages.controllers;

import com.lec.packages.domain.Member_Planner;
import com.lec.packages.service.MemberPlannerService;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/planner")
@RequiredArgsConstructor
public class MemberPlannerController {

    private final MemberPlannerService memberPlannerService;

    // 일정 추가
    @PostMapping("/add")
    public Member_Planner addPlanner(@RequestBody Member_Planner planner) {
        return memberPlannerService.savePlanner(planner);
    }

    // 특정 회원의 일정 조회
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getPlanners(@RequestParam("memId") String memId) {
        List<Member_Planner> planners = memberPlannerService.getAllPlanners(memId);

        List<Map<String, Object>> formattedPlanners = planners.stream().map(planner -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", planner.getPlanNo());
            map.put("title", planner.getPlanName());
            map.put("start", planner.getPlanDate().toString()); // ✅ JSON에서 `start`를 문자열로 변환
            map.put("planText", planner.getPlanText());
            map.put("planIschk", planner.isPlanIschk());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(formattedPlanners);
    }


    // 특정 날짜 일정 조회
    @GetMapping("/date")
    public List<Member_Planner> getPlannerByDate(@RequestParam("memId") String memId,@RequestParam("planDate") Date planDate) {
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
}
