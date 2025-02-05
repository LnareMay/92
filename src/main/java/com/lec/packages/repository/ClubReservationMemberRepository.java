package com.lec.packages.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lec.packages.domain.Member;
import com.lec.packages.domain.Reservation_Member_List;
import com.lec.packages.domain.primaryKeyClasses.ClubReservationMemberKeyClass;

public interface ClubReservationMemberRepository extends JpaRepository<Reservation_Member_List, ClubReservationMemberKeyClass>{

	@Query("SELECT m FROM Reservation_Member_List m WHERE m.memId = :memId")
	List<Reservation_Member_List> findByMemId(@Param("memId") String memId);
	
	Optional<Reservation_Member_List> findByClubCodeAndMemId(String clubCode,String memId);
}