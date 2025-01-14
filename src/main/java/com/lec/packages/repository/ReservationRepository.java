package com.lec.packages.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lec.packages.domain.Member;
import com.lec.packages.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, String>{
	 List<Reservation> findByMemId(String memId);

	List<Reservation> findByFacilityCode(String facilityCode);
}
