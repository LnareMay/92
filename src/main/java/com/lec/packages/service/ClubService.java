package com.lec.packages.service;

import com.lec.packages.dto.ClubBoardDTO;
import com.lec.packages.dto.ClubDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;

public interface ClubService {
	
	String create(ClubDTO clubDTO);
	String generateClubCode();
	ClubDTO detail(String clubCode);
	void modify(ClubDTO clubDTO);
	void delete(String clubCode);
	
	ClubDTO board(String clubCode);
  
	PageResponseDTO<ClubDTO> list(PageRequestDTO pageRequestDTO);

	int registerClubBoard(ClubBoardDTO clubBoardDTO);
}
