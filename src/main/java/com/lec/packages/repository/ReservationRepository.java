package com.lec.packages.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lec.packages.domain.Member;
import com.lec.packages.domain.Reservation;
import com.lec.packages.dto.ClubReservationInterface;

public interface ReservationRepository extends JpaRepository<Reservation, String> {

	Optional<Reservation> findByReservationCode(@Param("reservationCode") String reservationCode);

	Page<Reservation> searchByMemId(String memId, Pageable pageable);

	@Query("SELECT r FROM Reservation r " + "WHERE r.memId = :memId " + "ORDER BY " + "CASE r.reservationProgress "
			+ "    WHEN '예약진행중' THEN 1 " + "    WHEN '예약완료' THEN 2 " + "    WHEN '예약취소' THEN 3 " + "    ELSE 4 " + // 예상치
																													// 못한
																													// 상태를
																													// 처리
			"END, " + "r.CREATEDATE DESC")
	List<Reservation> findByMemId(@Param("memId") String memId);

	List<Reservation> findByFacilityCode(String facilityCode);

	// 사용자의 모든 시설에 대한 예약 조회 (서브쿼리 사용)
	@Query("SELECT r FROM Reservation r "
			+ "WHERE r.facilityCode IN (SELECT f.facilityCode FROM Facility f WHERE f.memId = :memId) "
			+ "ORDER BY r.reservationDate DESC")
	Page<Reservation> findAllReservationsWithUser(@Param("memId") String memId, Pageable pageable);

	// 시설 예약 내역 클럽원 현재 인원 수
	@Query(value = "select r.*, count(rml.MEM_ID) as nowMemCount from reservation r left join reservation_member_list rml on r.club_code = rml.CLUB_CODE and r.RESERVATION_CODE = rml.RESERVATION_CODE where r.club_code =:clubCode and r.RESERVATION_DATE > Now() and rml.DELETE_FLAG is not true group by r.RESERVATION_CODE", nativeQuery = true)
	List<ClubReservationInterface> getClubResList(@Param("clubCode") String clubCode);

	List<Reservation> findByFacilityCodeAndReservationDateAndDeleteFlagOrderByReservationStartTime(String facilityCode,
			Date reservationDate, boolean deleteFlag);

	// 예약 테이블의 memId를 통해 Member 테이블의 정보를 조회
	@Query("SELECT m FROM Member m WHERE m.memId = :memId")
	Member findMemberByReservationMemId(@Param("memId") String memId);

	// 로그인 한 유저의 예약 승인 된 시설 만 노출
	@Query("SELECT r FROM Reservation r "
			+ "WHERE r.facilityCode IN (SELECT f.facilityCode FROM Facility f WHERE f.memId = :memId) "
			+ "AND r.reservationProgress = '예약완료'")
	List<Reservation> findConfirmedReservationsByMemId(@Param("memId") String memId);

	// 예약코드로 시설이름, 시설운영시간 조회
	@Query("SELECT r.facilityName, r.reservationStartTime, r.reservationEndTime "
			+ "FROM Reservation r WHERE r.reservationCode = :reservationCode")
	List<Object[]> findFacilityAndTimesByCode(@Param("reservationCode") String reservationCode);

}
