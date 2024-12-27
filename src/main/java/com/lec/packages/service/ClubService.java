package com.lec.packages.service;

import java.util.List;
import java.util.stream.Collectors;
import com.lec.packages.domain.Club_Board;


import com.lec.packages.dto.ClubBoardDTO;
import com.lec.packages.dto.ClubDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;

public interface ClubService {
	
	void create(ClubDTO clubDTO, String storedFileName);
	String generateClubCode();
	ClubDTO detail(String clubCode);
	void modify(ClubDTO clubDTO);
//	void remove(ClubDTO clubDTO);
	void join(ClubDTO clubDTO);
	
	ClubDTO board(String clubCode);
  
	List<ClubDTO> ListByTheme(String clubTheme);
	List<ClubDTO> getAllClubs();
	PageResponseDTO<ClubDTO> list(PageRequestDTO pageRequestDTO);

	int registerClubBoard(ClubBoardDTO clubBoardDTO);

	default Club_Board dtoToEntity(ClubBoardDTO boardDTO) {
		
		Club_Board board = Club_Board.builder()
				.clubCode(boardDTO.getCLUB_CODE())
				.boardNo(boardDTO.getBOARD_NO())
				.boardType(boardDTO.getBOARD_TYPE())
				.boardText(boardDTO.getBOARD_TEXT())
				.memID(boardDTO.getMEM_ID())
				.build();
		
		if(boardDTO.getFileNames() != null) {
			boardDTO.getFileNames().forEach(fileName -> {
				String[] arr = fileName.split("_");
				board.addImage(arr[0], arr[1]);
			});
		}
		
		return board;
	}

	default ClubBoardDTO entityToDTO(Club_Board board) {
		
		ClubBoardDTO boardDTO = ClubBoardDTO.builder()
				.BOARD_NO(board.getBoardNo())
				.BOARD_TYPE(board.getBoardType())
				.BOARD_TEXT(board.getBoardText())
				.MEM_ID(board.getMemID())
				.MODIFYDATE(board.getMODIFYDATE())
				.CREATEDATE(board.getCREATEDATE())
				.build();
		
		List<String> fileNames = 
			board.getImages().stream().sorted().map(boardImage -> boardImage.getBoardImage()).collect(Collectors.toList());
		
		boardDTO.setFileNames(fileNames);
		
		return boardDTO;
	}
	

	


}
