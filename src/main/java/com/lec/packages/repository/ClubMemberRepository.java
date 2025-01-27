package com.lec.packages.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lec.packages.domain.Club_Member_List;
import com.lec.packages.domain.Member;
import com.lec.packages.domain.primaryKeyClasses.ClubMemberKeyClass;


public interface ClubMemberRepository extends JpaRepository<Club_Member_List, ClubMemberKeyClass> {

	@Query("SELECT cm.clubCode, COUNT(cm) FROM Club_Member_List cm WHERE cm.deleteFlag = false GROUP BY cm.clubCode")
	List<Object[]> countByClubCode();
    
    // 탈퇴된 회원 조회
    @Query("SELECT m FROM Club c JOIN FETCH Club_Member_List cm ON c.clubCode = cm.clubCode "
            + "JOIN FETCH Member m ON cm.memId = m.memId WHERE c.clubCode = :clubCode AND cm.deleteFlag = true")
    List<Member> findDeleteMember(@Param("clubCode") String clubCode);
    
    // 클럽가입 회원 상세조회
    @Query("SELECT m FROM Club c JOIN FETCH Club_Member_List cm ON c.clubCode = cm.clubCode "
    		+ "JOIN FETCH Member m ON cm.memId = m.memId WHERE c.clubCode = :clubCode AND cm.deleteFlag = false ORDER BY cm.CREATEDATE")
    List<Member> findMemberDetails(@Param("clubCode") String clubCode);
    
    // 클럽가입 회원 목록조회 페이징
    @Query("SELECT m FROM Club c JOIN FETCH Club_Member_List cm ON c.clubCode = cm.clubCode "
    		+ "JOIN FETCH Member m ON cm.memId = m.memId WHERE c.clubCode = :clubCode AND cm.deleteFlag = false ORDER BY cm.CREATEDATE")
    Page<Member> findMemberAll(@Param("clubCode") String clubCode, Pageable pageable);
    
    /*
    @Query("SELECT cm FROM Club_Member_List cm WHERE cm.deleteFlag = false AND cm.clubCode = :clubCode order by CREATEDATE")
    Page<Club_Member_List> findActiveClubMember(@Param("clubCode") String clubCode,  Pageable pageable);	
     
    @Query("SELECT m.memPicture FROM Member m WHERE m.memId = :memId")
	List<String> findPicturesByMemberId(@Param("memId") String memId);

     */
    
    /*
    @Query("SELECT m.memPicture FROM Club_Member_List cm JOIN Member m ON m.memId = cm.memId WHERE cm.clubCode = :clubCode") 
    List<String> findMemberPicture(@Param("clubCode") String clubCode);
    
    @Query("SELECT m.memPicture FROM Member m WHERE m.memId = :memId")
    Optional<String> findMemberPictureByMemId(@Param("memId") String memId);
    
	@Query("SELECT COALESCE(COUNT(cm), 0) FROM Club_Member_List cm WHERE cm.deleteFlag = false AND cm.clubCode = :clubCode"
	) Optional<Integer> countByClubCode(@Param("clubCode") String clubCode);
     */
    
}
