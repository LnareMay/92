package com.lec.admin.service;



import com.lec.admin.dto.PageRequestDTO;
import com.lec.admin.dto.PageResponseDTO;
import com.lec.admin.dto.facilityDTO;

public interface FacilityService {

	PageResponseDTO<facilityDTO> list(PageRequestDTO pageRequestDTO);

}
