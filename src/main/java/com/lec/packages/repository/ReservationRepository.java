package com.lec.packages.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lec.packages.domain.Member;
import com.lec.packages.domain.Reservation;
import com.lec.packages.dto.ClubReservationDTO;
import com.lec.packages.dto.ClubReservationInterface;

public interface ReservationRepository extends JpaRepository<Reservation, String>{
	 List<Reservation> findByMemId(String memId);

	List<Reservation> findByFacilityCode(String facilityCode);

	// 시설 예약 내역 클럽원 현재 인원 수
    @Query(value = "select r.*, count(rml.MEM_ID) as nowMemCount from reservation r join reservation_member_list rml on r.club_code = rml.CLUB_CODE where r.club_code =:clubCode and r.RESERVATION_DATE > Now() group by r.RESERVATION_CODE", nativeQuery = true)
    List<ClubReservationInterface> getClubResList(@Param("clubCode") String clubCode);

	List<Reservation> findByFacilityCodeAndReservationDateAndDeleteFlagOrderByReservationStartTime(String facilityCode, Date reservationDate, boolean deleteFlag);
}
