package com.lec.packages.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lec.packages.domain.Club_Member_List;
import com.lec.packages.domain.Member;


public interface ClubMemberRepository extends JpaRepository<Club_Member_List, String> {

	@Query("SELECT cm.clubCode, COUNT(cm) FROM Club_Member_List cm WHERE cm.deleteFlag = false GROUP BY cm.clubCode")
	List<Object[]> countByClubCode();
    
    // 탈퇴된 회원 조회
    @Query("SELECT m FROM Club c JOIN FETCH Club_Member_List cm ON c.clubCode = cm.clubCode "
            + "JOIN FETCH Member m ON cm.memId = m.memId WHERE c.clubCode = :clubCode AND cm.deleteFlag = true")
    List<Member> findDeleteMember(@Param("clubCode") String clubCode);
    
    @Query("SELECT cm FROM Club_Member_List cm WHERE cm.memId = :memId AND cm.clubCode = :clubCode")
    Optional<Club_Member_List> findJoinMember(@Param("memId") String memId, @Param("clubCode") String clubCode);
    
    // 클럽가입 회원 상세조회
    @Query("SELECT m FROM Club c JOIN FETCH Club_Member_List cm ON c.clubCode = cm.clubCode "
    		+ "JOIN FETCH Member m ON cm.memId = m.memId WHERE c.clubCode = :clubCode AND cm.deleteFlag = false ORDER BY cm.CREATEDATE")
    List<Member> findMemberDetails(@Param("clubCode") String clubCode);
    
    // 클럽가입 회원 목록조회 페이징
    @Query("SELECT m FROM Club c JOIN FETCH Club_Member_List cm ON c.clubCode = cm.clubCode "
    		+ "JOIN FETCH Member m ON cm.memId = m.memId WHERE c.clubCode = :clubCode AND cm.deleteFlag = false ORDER BY cm.CREATEDATE")
    Page<Member> findMemberAll(@Param("clubCode") String clubCode, Pageable pageable);
    
    // 클럽별 회원 신고 수 
    @Query("SELECT cm.memId, cm.clubCode, cm.reportCount FROM Club_Member_List cm WHERE cm.clubCode = :clubCode")
    List<Object[]> findReportCount(@Param("clubCode") String clubCode);
}
