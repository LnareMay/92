package com.lec.packages.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lec.packages.domain.Club_Member_List;
import com.lec.packages.domain.primaryKeyClasses.ClubMemberKeyClass;
import com.lec.packages.dto.ClubDTO;


public interface ClubMemberRepository extends JpaRepository<Club_Member_List, ClubMemberKeyClass> {

	@Query("SELECT cm.clubCode, COUNT(cm) FROM Club_Member_List cm WHERE cm.deleteFlag = false GROUP BY cm.clubCode")
	List<Object[]> countByClubCode();
    
    boolean existsByMemIdAndClubCode(String memId, String clubCode);
    
    /*
    @Query("SELECT cm FROM Club_Member_List cm WHERE cm.deleteFlag = false AND cm.clubCode = :clubCode order by CREATEDATE")
    Page<Club_Member_List> findActiveClubMember(@Param("clubCode") String clubCode,  Pageable pageable);	
     
    @Query("SELECT m.memPicture FROM Member m WHERE m.memId = :memId")
	List<String> findPicturesByMemberId(@Param("memId") String memId);
    
    @Query("SELECT m.memNickname FROM Member m WHERE m.memId = :memId")
    List<String> findNicknameByMemberId(@Param("memId") String memId);
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
