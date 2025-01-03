package com.lec.packages.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.lec.packages.domain.Club;
import com.lec.packages.domain.Club_Board;
import com.lec.packages.domain.Club_Board_Reply;
import com.lec.packages.domain.primaryKeyClasses.ClubBoardKeyClass;
import com.lec.packages.domain.primaryKeyClasses.ClubBoardReplyKeyClass;
import com.lec.packages.dto.ClubBoardAllListDTO;
import com.lec.packages.dto.ClubBoardDTO;
import com.lec.packages.dto.ClubBoardReplyDTO;
import com.lec.packages.dto.ClubDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.repository.ClubBoardReplyRepository;
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
	private final ClubBoardReplyRepository clubBoardReplyRepository;
	
	// 클럽생성
	public void create(ClubDTO clubDTO) {
		String clubCode = generateClubCode();
		clubDTO.setClubCode(clubCode);
		Club club = modelMapper.map(clubDTO, Club.class);
		club.getClubImage1();


//		String saveCode = clubRepository.save(club).getClubCode();
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
		String[] keywords = pageRequestDTO.getKeywords();
		Pageable pageable = pageRequestDTO.getPageable("clubCode");
		
		Page<Club> result = clubRepository.searchAllImpl(types, keywords, pageable);
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
	
	// 클럽테마리스트
	 @Override
		public PageResponseDTO<ClubDTO> ListByTheme(PageRequestDTO pageRequestDTO, String clubTheme) {
		 	
		 	List<Club> filteredClubs = clubRepository.findByClubThemeContaining(clubTheme);
		 	
		 	int total = filteredClubs.size();
		 	
		 	if (total == 0) {
		 		return PageResponseDTO.<ClubDTO>withAll()
		 							  .pageRequestDTO(pageRequestDTO)
		 							  .dtoList(Collections.emptyList())
		 							  .total(0)
		 							  .build();
		 	}
		 	
		 	int start = (pageRequestDTO.getPage() - 1) * pageRequestDTO.getSize();
		 	int end = Math.min(start + pageRequestDTO.getSize(), total);
		 	
		 	if (start < 0 || start >= total) {
		 		start = 0;
		 	}
	        
	        List<ClubDTO> clubList = filteredClubs.subList(start, end)
	        							   .stream()
	        							   .map(club -> modelMapper.map(club, ClubDTO.class))
	        							   .collect(Collectors.toList());
	        
	        return PageResponseDTO.<ClubDTO>withAll()
	                    .pageRequestDTO(pageRequestDTO)
	                    .dtoList(clubList)
	                    .total(total)
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
	
	// 클럽삭제
//	@Override
//	public void remove(ClubDTO clubDTO) {
//		Optional<Club> result = clubRepository.findById(clubDTO.getClubCode());
//		Club club = result.orElseThrow();
//		
//		club.remove(clubDTO.isDeleteFlag());
//		clubRepository.save(club);
//	}
	

	 
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
		int boardNo = 0;
		if(boardNoResult.isPresent()){
			Club_Board club_Board = boardNoResult.orElseThrow();
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

	@Override
	public PageResponseDTO<ClubBoardAllListDTO> listWithAll(PageRequestDTO pageRequestDTO, String clubCode) {
		
		String[] types = pageRequestDTO.getTypes();
		Pageable pageable = pageRequestDTO.getPageable("boardNo");

		Page<ClubBoardAllListDTO> result = clubBoardRepository.searchWithAll(types, pageable, clubCode);

		return PageResponseDTO.<ClubBoardAllListDTO>withAll()
				.pageRequestDTO(pageRequestDTO)
				.dtoList(result.getContent())
				.total((int)result.getTotalElements())
				.build();
	}

	@Override
	public ClubBoardDTO readOne(int boardNo, String clubCode) {
		Optional<Club_Board> resultBoard = clubBoardRepository.findBoardByImages(clubCode, boardNo);
		Club_Board club_Board = resultBoard.orElseThrow();

		ClubBoardDTO clubBoardDTO = entityToDTO(club_Board);

		return clubBoardDTO;
	}

	@Override
	public int registerReply(ClubBoardReplyDTO clubBoardReplyDTO) {
		Optional<Club_Board_Reply> lastReply = clubBoardReplyRepository.findReplyNoByKey(clubBoardReplyDTO.getClubCode(), clubBoardReplyDTO.getBoardNo());

		int lastReplyNo = 0;
		if(lastReply.isPresent()){
			Club_Board_Reply temp = lastReply.orElseThrow();
			log.info(temp);
			lastReplyNo = temp.getReplyNo();
			temp = null;
		}

		lastReplyNo += 1;
		clubBoardReplyDTO.setReplyNo(lastReplyNo);

		Club_Board_Reply board_Reply = modelMapper.map(clubBoardReplyDTO, Club_Board_Reply.class);
		log.info(board_Reply);
		int replyNo = clubBoardReplyRepository.save(board_Reply).getReplyNo();
		return replyNo;
	}

	@Override
	public PageResponseDTO<ClubBoardReplyDTO> getReplyListOfBoard(int boardNo, String clubCode,
			PageRequestDTO pageRequestDTO) {
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() <= 0 ? 0 : pageRequestDTO.getPage() -1,
							pageRequestDTO.getSize(), Sort.by("replyNo").ascending());
		
		Page<Club_Board_Reply> result = clubBoardReplyRepository.listOfReply(boardNo, clubCode, pageable);
		List<ClubBoardReplyDTO> dtoList = result.getContent().stream().map(reply -> modelMapper.map(reply, ClubBoardReplyDTO.class)).collect(Collectors.toList());

		return PageResponseDTO.<ClubBoardReplyDTO>withAll()
				.pageRequestDTO(pageRequestDTO)
				.dtoList(dtoList)
				.total((int) result.getTotalElements())
				.build();
	}

	@Override
	public ClubBoardReplyDTO readReply(String clubCode, int boardNo, int replyNo) {
		ClubBoardReplyKeyClass keyClass = new ClubBoardReplyKeyClass();
		keyClass.setBoardNo(boardNo);
		keyClass.setClubCode(clubCode);
		keyClass.setReplyNo(replyNo);

		Optional<Club_Board_Reply> result = clubBoardReplyRepository.findById(keyClass);
		Club_Board_Reply reply = result.orElseThrow();
		return modelMapper.map(reply, ClubBoardReplyDTO.class);
	}

	@Override
	public void modifyReply(ClubBoardReplyDTO replyDTO) {
		ClubBoardReplyKeyClass keyClass = new ClubBoardReplyKeyClass();
		keyClass.setBoardNo(replyDTO.getBoardNo());
		keyClass.setClubCode(replyDTO.getClubCode());
		keyClass.setReplyNo(replyDTO.getReplyNo());

		Optional<Club_Board_Reply> result = clubBoardReplyRepository.findById(keyClass);
		Club_Board_Reply reply = result.orElseThrow();
		reply.changeText(replyDTO.getBoardText());
		log.info(reply);
		clubBoardReplyRepository.save(reply);
	}

	@Override
	public int deleteReply(String clubCode, int boardNo, int replyNo) {
		ClubBoardReplyKeyClass keyClass = new ClubBoardReplyKeyClass();
		keyClass.setBoardNo(boardNo);
		keyClass.setClubCode(clubCode);
		keyClass.setReplyNo(replyNo);

		Optional<Club_Board_Reply> result = clubBoardReplyRepository.findById(keyClass);
		Club_Board_Reply reply = result.orElseThrow();

		reply.setDeleteFlag(true);
		log.info(reply);
		clubBoardReplyRepository.save(reply);

		return reply.getReplyNo();
	}

	@Override
	public ClubBoardDTO modifyClubBoard(ClubBoardDTO clubBoardDTO) {
		ClubBoardKeyClass keyClass = new ClubBoardKeyClass();
		keyClass.setBoardNo(clubBoardDTO.getBOARD_NO());
		keyClass.setClubCode(clubBoardDTO.getCLUB_CODE());

		Optional<Club_Board> result = clubBoardRepository.findById(keyClass);
		Club_Board club_Board = result.orElseThrow();
		club_Board.change(clubBoardDTO.getBOARD_TYPE(), clubBoardDTO.getBOARD_TEXT());

		club_Board.clearImage();
		
		if(clubBoardDTO.getFileNames() != null) {
			for(String fileName:clubBoardDTO.getFileNames()) {
				String[] arr = fileName.split("_");
				club_Board.addImage(arr[0], arr[1]);
			}
		}

		clubBoardRepository.save(club_Board);
		
		return clubBoardDTO;
	}

	@Override
	public String removeClubBoard(ClubBoardDTO clubBoardDTO) {
		ClubBoardKeyClass keyClass = new ClubBoardKeyClass();
		keyClass.setBoardNo(clubBoardDTO.getBOARD_NO());
		keyClass.setClubCode(clubBoardDTO.getCLUB_CODE());

		Optional<Club_Board> result = clubBoardRepository.findById(keyClass);
		Club_Board club_Board = result.orElseThrow();
		club_Board.setDELETE_FLAG(true);
		log.info(club_Board);
		String clubCode = clubBoardRepository.save(club_Board).getClubCode();

		return clubCode;
	}



}
