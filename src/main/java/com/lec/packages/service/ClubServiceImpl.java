package com.lec.packages.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lec.packages.domain.Club;
import com.lec.packages.domain.Club_Board;
import com.lec.packages.dto.ClubBoardDTO;
import com.lec.packages.dto.ClubDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.repository.ClubBoardRepository;
import com.lec.packages.repository.ClubRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class ClubServiceImpl implements ClubService {

	private final ModelMapper modelMapper;
	private final ClubRepository clubRepository;
	private final ClubBoardRepository clubBoardRepository;
	
	public String create(ClubDTO clubDTO) {
		String clubCode = generateClubCode();
		clubDTO.setClubCode(clubCode);
		Club club = modelMapper.map(clubDTO, Club.class);
		
		String saveCode = clubRepository.save(club).getClubCode();
		return saveCode;		
	}
	
	// 클럽코드생성
	@Override
	public String generateClubCode() {
		String clubCode;
		do {
			clubCode = String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 1000000));
		} while (clubRepository.existsById(clubCode)); 
		return clubCode;
	}

	@Override
	public PageResponseDTO<ClubDTO> list(PageRequestDTO pageRequestDTO) {
		String[] types = pageRequestDTO.getTypes();
		String keyword = pageRequestDTO.getKeyword();
		Pageable pageable = pageRequestDTO.getPageable("clubCode");
		
		Page<Club> result = clubRepository.searchAllImpl(types, keyword, pageable);
		List<ClubDTO> clubList = result.getContent()
									   .stream()
									   .map(club -> modelMapper.map(club, ClubDTO.class))
									   .collect(Collectors.toList());
		
		
	    return PageResponseDTO.<ClubDTO>withAll()
	    		.pageRequestDTO(pageRequestDTO)
                .dtoList(clubList)
                .total((int)result.getTotalElements())
                .build();
	}
	
	public int registerClubBoard(ClubBoardDTO clubBoardDTO){
		String code = clubBoardDTO.getCLUB_CODE();
		Optional<Club_Board> boardNoResult = clubBoardRepository.findByClubCode(code);

		log.info(boardNoResult);
		Club_Board club_Board = boardNoResult.orElseThrow();
		int boardNo = 0;
		if (club_Board != null) {
			boardNo = club_Board.getBoardNo();
		}

		boardNo += 1;
		clubBoardDTO.setBOARD_NO(boardNo);

		// Club_Board saveClubBoard = modelMapper.map(clubBoardDTO, Club_Board.class);
		// log.info(saveClubBoard);
		// saveClubBoard.getImages().forEach(image -> log.info(image.getBoardImage()));
		Club_Board saveClubBoard = dtoToEntity(clubBoardDTO);
		saveClubBoard.getImages().forEach(image -> log.info(image.getBoardImage()));
		int resultBoardNo = clubBoardRepository.save(saveClubBoard).getBoardNo();
		
		return resultBoardNo;
	}
}
