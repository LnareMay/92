package com.lec.packages.service;


import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.lec.packages.domain.Reservation;
import com.lec.packages.domain.TransferHistory;
import com.lec.packages.dto.FacilityDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.dto.ReservationDTO;
import com.lec.packages.dto.TransferHistoryDTO;


public interface FacilityService {

	String register(FacilityDTO facilityDTO);
	PageResponseDTO<FacilityDTO> listByUser(String userId, PageRequestDTO pageRequestDTO);
//	PageResponseDTO<FacilityDTO> list(PageRequestDTO pageRequestDTO);
	FacilityDTO getFacilityByCode(String facilityCode);
	void modify(FacilityDTO facilityDTO);
	void bookByMember(TransferHistoryDTO transferHistoryDTO, ReservationDTO reservationDTO,BigDecimal memMoney);
	List<ReservationDTO> getReservationsByFacilityCode(String facilityCode); 
	
	PageResponseDTO<FacilityDTO> listAllFacility(PageRequestDTO pageRequestDTO,String facilityAddress, String exerciseCode);
	void remove(String facilityCode);


	FacilityDTO getFacilityBylistByUser(String username);

	PageResponseDTO<FacilityDTO> listAllFacility(PageRequestDTO pageRequestDTO
			,String facilityAddress, String exerciseCode, Boolean facilityIsOnlyClub);
	
    List<Reservation> getReservationTimeList(String facilityCode, Date reservationDate);
	void cancelBooking(String memId, TransferHistoryDTO transferHistoryDTO, ReservationDTO reservationDTO);


	
	 List<FacilityDTO> getFacilityCodeByUser(String memId);




}