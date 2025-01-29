package com.lec.packages.repository;

import com.lec.packages.domain.Member_Planner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberPlannerRepository extends JpaRepository<Member_Planner, Integer> {

    // 특정 회원이 등록한 일정 조회
    List<Member_Planner> findByMemId(String memId);

    // 특정 회원의 특정 날짜 일정 조회
    List<Member_Planner> findByMemIdAndPlanDate(String memId, Date planDate);
    
    Optional<Member_Planner> findById(int planNo);

}
