package com.lec.packages.service;

import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.dto.facilityDTO;

public interface FacilityService {

	PageResponseDTO<facilityDTO> list(PageRequestDTO pageRequestDTO);

}
