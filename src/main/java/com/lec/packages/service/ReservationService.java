package com.lec.packages.service;


import com.lec.packages.dto.MemberSecurityDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.dto.ReservationDTO;

public interface ReservationService {

	// PageResponseDTO<ReservationDTO> getReservationByFacilityCode(String facilityCode, PageRequestDTO pageRequestDTO);

	PageResponseDTO<ReservationDTO> getAllReservationsForUser(String memId, PageRequestDTO pageRequestDTO);

	ReservationDTO getReservationByCode(String reservationCode);

//	MemberSecurityDTO getMemberInfoByReservationCode(String reservationCode);

	
	
}
