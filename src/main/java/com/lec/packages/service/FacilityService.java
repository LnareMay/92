package com.lec.packages.service;



import com.lec.packages.dto.FacilityDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;


public interface FacilityService {

	String register(FacilityDTO facilityDTO);
	PageResponseDTO<FacilityDTO> listByUser(String userId, PageRequestDTO pageRequestDTO);
	PageResponseDTO<FacilityDTO> list(PageRequestDTO pageRequestDTO);
	FacilityDTO getFacilityByCode(String facilityCode);
	void modify(FacilityDTO facilityDTO);


}
