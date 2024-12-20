package com.lec.packages.service;

import com.lec.packages.dto.ClubDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;

public interface ClubService {
	
	String create(ClubDTO clubDTO);
	String generateClubCode();

	PageResponseDTO<ClubDTO> list(PageRequestDTO pageRequestDTO);

}
