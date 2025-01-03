package com.lec.packages.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

import com.lec.packages.domain.Club_Board;
import com.lec.packages.dto.ClubBoardAllListDTO;
import com.lec.packages.dto.ClubBoardDTO;
import com.lec.packages.dto.ClubBoardReplyDTO;
import com.lec.packages.dto.ClubDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;

import jakarta.validation.Valid;

public interface ClubService {
	
	void create(ClubDTO clubDTO);
	void updateImages(String clubCode, ClubDTO clubDTO);
	
	String generateClubCode();
	ClubDTO detail(String clubCode);
	void modify(ClubDTO clubDTO);

	void remove(String clubCode);
	
	PageResponseDTO<ClubDTO> listByTheme(PageRequestDTO pageRequestDTO, String clubTheme);
	List<ClubDTO> getAllClubs();	
	

	ClubDTO board(String clubCode);
	ClubBoardDTO readOne(int boardNo, String clubCode);
	
	PageResponseDTO<ClubBoardAllListDTO> listWithAll(PageRequestDTO pageRequestDTO, String clubCode);
	PageResponseDTO<ClubDTO> list(PageRequestDTO pageRequestDTO);

	int registerClubBoard(ClubBoardDTO clubBoardDTO);

	int registerReply(ClubBoardReplyDTO clubBoardReplyDTO);

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
				System.out.println(fileName);
				String[] arr = fileName.split("_");
				board.addImage(arr[0], arr[1]);
			});
		}
		
		return board;
	}

	default ClubBoardDTO entityToDTO(Club_Board board) {
		
		ClubBoardDTO boardDTO = ClubBoardDTO.builder()
				.CLUB_CODE(board.getClubCode())
				.BOARD_NO(board.getBoardNo())
				.BOARD_TYPE(board.getBoardType())
				.BOARD_TEXT(board.getBoardText())
				.MEM_ID(board.getMemID())
				.MODIFYDATE(board.getMODIFYDATE())
				.CREATEDATE(board.getCREATEDATE())
				.build();
		
		List<String> fileNames = 
			board.getImages().stream().sorted().map(boardImage -> boardImage.getUuid() + "_" + boardImage.getBoardImage()).collect(Collectors.toList());
		
		boardDTO.setFileNames(fileNames);
		
		return boardDTO;
	}
    PageResponseDTO<ClubBoardReplyDTO> getReplyListOfBoard(int boardNo, String clubCode, PageRequestDTO pageRequestDTO);
	
    ClubBoardReplyDTO readReply(String clubCode, int boardNo, int replyNo);
    void modifyReply(ClubBoardReplyDTO replyDTO);
    int deleteReply(String clubCode, int boardNo, int replyNo);

    ClubBoardDTO modifyClubBoard(ClubBoardDTO clubBoardDTO);
    String removeClubBoard(ClubBoardDTO clubBoardDTO);
}
