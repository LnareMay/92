package com.lec.packages.service;

import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.dto.ReservationDTO;

public interface ReservationService {

	PageResponseDTO<ReservationDTO> ReservationlistByUser(String memId, PageRequestDTO pageRequestDTO);
}
