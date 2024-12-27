package com.lec.packages.service;

import java.util.Arrays;
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
	
	// 클럽생성
	public void create(ClubDTO clubDTO, String storedFileName) {
		String clubCode = generateClubCode();
		clubDTO.setClubCode(clubCode);
		Club club = modelMapper.map(clubDTO, Club.class);
		club.setClubImage1(storedFileName);

		String saveCode = clubRepository.save(club).getClubCode();
		clubRepository.save(club);		
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

	// 클럽리스트
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
	
	// 클럽상세보기
	@Override
	public ClubDTO detail(String clubCode) {
		Optional<Club> result = clubRepository.findById(clubCode);
		Club club = result.orElseThrow();
		ClubDTO clubDTO = modelMapper.map(club, ClubDTO.class);
		
		String originthemeName = clubDTO.getClubTheme();
		if(originthemeName != null && !originthemeName.isEmpty()) {
			String themeName = mapThemes(originthemeName);
			clubDTO.setThemeName(themeName);			
		}
		
		return clubDTO;
	}
	
	// 클럽상세보기 테마이름
	private String mapThemes(String clubTheme) {
		List<String> themes = Arrays.asList(clubTheme.split(","));
		return themes.stream()
					 .map(this::mapTheme)
					 .collect(Collectors.joining(" "));
	}
	
	private String mapTheme(String clubTheme) {
		switch (clubTheme.trim()) {
		case "THM_SPORTCLUB": return "#운동클럽";
		case "THM_SPORTJOURNAL": return "#운동일지";
		case "THM_FOOD": return "#영양제/식단";
		case "THM_FITNESS": return "#헬스";
		case "THM_DIET": return "#체중감량";
		case "THM_BODYPROFILE": return "#바디프로필";
		case "THM_RECOVER": return "#부상/재활";
		case "THM_CONTEST": return "#대회준비";
		case "THM_EXAM": return "#입시준비";
		
		default: return clubTheme;
		}
	}

	// 클럽수정
	@Override
	public void modify(ClubDTO clubDTO) {
		Optional<Club> result = clubRepository.findById(clubDTO.getClubCode());
		Club club = result.orElseThrow();
		club.change(clubDTO.getClubIntroduction(), clubDTO.getClubAddress()
				   ,clubDTO.getClubName(), clubDTO.getClubTheme()
				   ,clubDTO.getClubExercise(), clubDTO.getClubPw()
				   ,clubDTO.isClubIsprivate());
		clubRepository.save(club);
	}
	
	// 클럽 가입하기
	@Override
	public void join(ClubDTO clubDTO) {
		Optional<Club> result = clubRepository.findById(clubDTO.getClubCode());
		Club club = result.orElseThrow();
		club.join(clubDTO.getClubCode(), clubDTO.getMemId());
		clubRepository.save(club);
	}

	
	// 클럽삭제
//	@Override
//	public void remove(ClubDTO clubDTO) {
//		Optional<Club> result = clubRepository.findById(clubDTO.getClubCode());
//		Club club = result.orElseThrow();
//		
//		club.remove(clubDTO.isDeleteFlag());
//		clubRepository.save(club);
//	}
	
	// 클럽 테마별 리스트
	 @Override
	    public List<ClubDTO> ListByTheme(String clubTheme) {
	        List<Club> clubs = clubRepository.findByClubThemeContaining(clubTheme);
	        return clubs.stream()
	                    .map(club -> modelMapper.map(club, ClubDTO.class))
	                    .collect(Collectors.toList());
	    }
	 
	// 클럽 전체리스트 
	@Override
	public List<ClubDTO> getAllClubs() {
		List<Club> clubs = clubRepository.findAll();
		
        return clubs.stream()
                .map(club -> modelMapper.map(club, ClubDTO.class))
                .collect(Collectors.toList());
	}



	// 클럽게시판
	@Override
	public ClubDTO board(String clubCode) {
		Optional<Club> result = clubRepository.findById(clubCode);
		Club club = result.orElseThrow();
		ClubDTO clubDTO = modelMapper.map(club, ClubDTO.class);
		
		return clubDTO;
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
