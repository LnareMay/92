package com.lec.packages.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lec.packages.domain.Club_Member_List;
import com.lec.packages.domain.primaryKeyClasses.ClubMemberKeyClass;


public interface ClubMemberRepository extends JpaRepository<Club_Member_List, ClubMemberKeyClass> {

    @Query("SELECT cm FROM Club_Member_List cm WHERE cm.deleteFlag = false AND cm.clubCode = :clubCode order by CREATEDATE")
    Page<Club_Member_List> findActiveClubMember(@Param("clubCode") String clubCode,  Pageable pageable);
   
    @Query("SELECT COALESCE(COUNT(cm), 0) FROM Club_Member_List cm WHERE cm.deleteFlag = false AND cm.clubCode = :clubCode")
    Optional<Integer> countByClubCode(@Param("clubCode") String clubCode);
    
}
