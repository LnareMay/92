package com.lec.packages.service;


import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.lec.packages.dto.FacilityDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.dto.ReservationDTO;


public interface FacilityService {

	String register(FacilityDTO facilityDTO);
	PageResponseDTO<FacilityDTO> listByUser(String userId, PageRequestDTO pageRequestDTO);
	PageResponseDTO<FacilityDTO> list(PageRequestDTO pageRequestDTO);
	FacilityDTO getFacilityByCode(String facilityCode);
	void modify(FacilityDTO facilityDTO);
	void bookByMember(ReservationDTO reservationDTO);
	List<ReservationDTO> getReservationsByFacilityCode(String facilityCode); 
	
	PageResponseDTO<FacilityDTO> listAllFacility(PageRequestDTO pageRequestDTO
			,String facilityAddress, String exerciseCode);
}