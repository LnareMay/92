package com.lec.packages.service;

import java.sql.Date;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.lec.packages.domain.Club;
import com.lec.packages.domain.Club_Board;
import com.lec.packages.domain.Club_Board_Reply;
import com.lec.packages.domain.Club_Member_List;
import com.lec.packages.domain.Member;
import com.lec.packages.domain.Reservation;
import com.lec.packages.domain.Reservation_Member_List;
import com.lec.packages.domain.primaryKeyClasses.ClubBoardKeyClass;
import com.lec.packages.domain.primaryKeyClasses.ClubBoardReplyKeyClass;
import com.lec.packages.domain.primaryKeyClasses.ClubMemberKeyClass;
import com.lec.packages.domain.primaryKeyClasses.ClubReservationMemberKeyClass;
import com.lec.packages.dto.ClubBoardAllListDTO;
import com.lec.packages.dto.ClubBoardDTO;
import com.lec.packages.dto.ClubBoardReplyDTO;
import com.lec.packages.dto.ClubDTO;
import com.lec.packages.dto.ClubReservationDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.repository.ClubBoardReplyRepository;
import com.lec.packages.repository.ClubBoardRepository;
import com.lec.packages.repository.ClubMemberRepository;
import com.lec.packages.repository.ClubRepository;
import com.lec.packages.repository.ClubReservationMemberRepository;
import com.lec.packages.repository.ReservationRepository;
import com.lec.packages.dto.ClubReservationInterface;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class ClubServiceImpl implements ClubService {

	private final ModelMapper modelMapper;
	@Autowired
	private final ClubRepository clubRepository;
	
	private final ClubBoardRepository clubBoardRepository;
	private final ClubBoardReplyRepository clubBoardReplyRepository;
	private final ClubMemberRepository clubMemberRepository;
	private final ReservationRepository reservationRepository;
	private final ClubReservationMemberRepository clubReservationMemberRepository;
	
	
	// 클럽생성
	public String create(ClubDTO clubDTO) {
		String clubCode = generateClubCode();
		clubDTO.setClubCode(clubCode);
		
		Club club = modelMapper.map(clubDTO, Club.class);	
		clubRepository.save(club);	

		return clubCode;
	}
	
	/* 클럽수정 이미지변경
	@Override
	public void updateImages(String clubCode, ClubDTO clubDTO) {
		Optional<Club> optionalClub = clubRepository.findById(clubCode);
		if (optionalClub.isPresent()) {
			Club club = optionalClub.get();
			club.setClubImage1(clubDTO.getClubImage1());
			club.setClubImage2(clubDTO.getClubImage2());
			club.setClubImage3(clubDTO.getClubImage3());
			club.setClubImage4(clubDTO.getClubImage4());
			clubRepository.save(club);
		}
	}  */

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
        Pageable pageable = pageRequestDTO.getPageable("clubCode");
        Page<Club> result = clubRepository.findAllActiveClubs(pageable);        

        Map<String, Integer> membercountmap = membercount();        
        List<ClubDTO> dtoList = result.getContent().stream()
                                      .map(club -> {
                                    	ClubDTO clubDTO = modelMapper.map(club, ClubDTO.class);
                                    	if (clubDTO.getClubTheme() == null) {
                                            clubDTO.setClubTheme("ALL");
                                        }
                                    	clubDTO.setMemberCount(membercountmap.getOrDefault(club.getClubCode(), 0));
                                    	return clubDTO;
                                      })
                                      .collect(Collectors.toList());

        return PageResponseDTO.<ClubDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }
    
	// 클럽리스트 : 테마별
    @Override
    public PageResponseDTO<ClubDTO> listByTheme(PageRequestDTO pageRequestDTO, String clubTheme) {

    	Pageable pageable = pageRequestDTO.getPageable("clubCode");
        Page<Club> filteredClubs = clubRepository.findByClubThemeContaining(clubTheme, pageable);

        Map<String, Integer> membercountmap = membercount();
        List<ClubDTO> clubList = filteredClubs.getContent()
							                .stream()
							                .map(club -> {
							                	ClubDTO clubDTO = modelMapper.map(club, ClubDTO.class);
							                	clubDTO.setMemberCount(membercountmap.getOrDefault(club.getClubCode(), 0));
							                	return clubDTO;
							                  })
							                .collect(Collectors.toList());

        return PageResponseDTO.<ClubDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(clubList)
                .total((int)filteredClubs.getTotalElements())
                .build();
    }

    // 클럽리스트 : 주소&테마별
    @Override
    public PageResponseDTO<ClubDTO> listByAddressAndTheme(PageRequestDTO pageRequestDTO, String memAddressSet, String clubTheme) {
        Pageable pageable = pageRequestDTO.getPageable("clubCode");

        // 검색 필터링
        String[] types = {"address", "theme"};
        String[] keywords = {memAddressSet, clubTheme};

        Page<Club> result = clubRepository.searchAllImpl(types, keywords, pageable);

        Map<String, Integer> membercountmap = membercount();
        List<ClubDTO> dtoList = result.getContent().stream()
                                      .map(club -> {
                                          ClubDTO clubDTO = modelMapper.map(club, ClubDTO.class);
                                          clubDTO.setMemberCount(membercountmap.getOrDefault(club.getClubCode(), 0));
                                          
                                          if (clubDTO.getClubTheme() == null) {
                                              clubDTO.setClubTheme("ALL");
                                      }
                                          return clubDTO;
                                      })
                                      .collect(Collectors.toList());
        log.info("=== Keywords==== : {}, {}", keywords[0], keywords[1]);        

        return PageResponseDTO.<ClubDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }       
    
	// 클럽상세보기
	@Override
	public ClubDTO detail(String clubCode) {
		Club club = clubRepository.findById(clubCode).orElseThrow();

		ClubDTO clubDTO = modelMapper.map(club, ClubDTO.class);
		
		String originthemeName = clubDTO.getClubTheme();
		if(originthemeName != null && !originthemeName.isEmpty()) {
			String themeName = mapThemes(originthemeName);
			clubDTO.setThemeName(themeName);			
		}
		
		Map<String, Integer> memberCountmap = membercount();
		int memberCount = memberCountmap.getOrDefault(clubCode, 0);
		clubDTO.setMemberCount(memberCount);
		
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

	    if (clubDTO.getClubImage1() == null) clubDTO.setClubImage1(club.getClubImage1()); 
	    if (clubDTO.getClubImage2() == null) clubDTO.setClubImage2(club.getClubImage2()); 
	    if (clubDTO.getClubImage3() == null) clubDTO.setClubImage3(club.getClubImage3()); 
	    if (clubDTO.getClubImage4() == null) clubDTO.setClubImage4(club.getClubImage4()); 

	    // 클럽 정보 업데이트
	    club.change(
	        clubDTO.getClubIntroduction(),
	        clubDTO.getClubAddress(),
	        clubDTO.getClubName(),
	        clubDTO.getClubTheme(),
	        clubDTO.getClubExercise(),
	        clubDTO.getClubPw(),
	        clubDTO.isClubIsprivate(),
	        clubDTO.getClubImage1(),
	        clubDTO.getClubImage2(),
	        clubDTO.getClubImage3(),
	        clubDTO.getClubImage4()
	    );

	    // 클럽 정보 저장
	    clubRepository.save(club);
	}

	// 클럽삭제
	@Override
	public void remove(String clubCode) {
		Club club = clubRepository.findById(clubCode)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클럽입니다."));
		
		club.setDeleteFlag(true);
		
		clubRepository.save(club);
	}
	
	// 클럽가입하기
	@Override
	public void join(String memId, String clubCode, String clubPw) {		
		Club club = clubRepository.findById(clubCode)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 클럽입니다."));
		// 비공개 클럽일때
		if (club.isClubIsprivate()) {
			if (club.getClubPw() == null || !club.getClubPw().equals(clubPw)) {
				throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
			}
		}
		
		Club_Member_List clubMember = new Club_Member_List();
		clubMember.setMemId(memId);
		clubMember.setClubCode(clubCode);
		
	    clubMemberRepository.save(clubMember);		
	}
	
	// 클럽가입시 이미가입되어있는 회원인지 확인
		@Override
		public boolean isJoinMember(String memId, String clubCode) {
			
			List<Member> joinMembers = clubMemberRepository.findMemberDetails(clubCode);

		    return joinMembers.stream()
		            .anyMatch(member -> member.getMemId().equals(memId));			
		}
	
	// 클럽탈퇴
		@Override
		public void joindelete(String memId, String clubCode) {

		    ClubMemberKeyClass keyClass = new ClubMemberKeyClass(memId, clubCode);		    
		    Club_Member_List clubMember = clubMemberRepository.findById(keyClass)
		            .orElseThrow(() -> new IllegalArgumentException("해당 클럽 멤버를 찾을 수 없습니다."));
		    
		    clubMember.setDeleteFlag(true);
		    clubMemberRepository.save(clubMember);
		}

	// 클럽탈퇴시 이미탈퇴되어있는 회원인지 확인
		@Override
		public boolean isJoinDeleteMember(String memId, String clubCode) {

		    List<Member> deletedMembers = clubMemberRepository.findDeleteMember(clubCode);

		    return deletedMembers.stream()
		            .anyMatch(member -> member.getMemId().equals(memId));
		}	
		
    // 클럽멤버 목록조회 
	@Override
	public PageResponseDTO<Member> findMemberAll(String clubCode, PageRequestDTO pageRequestDTO) {
		Pageable pageable = pageRequestDTO.getPageable("clubCode");
		Page<Member> result = clubMemberRepository.findMemberAll(clubCode, pageable);
		
		return PageResponseDTO.<Member>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int) result.getTotalElements())
                .build();
	}

	@Override
	public List<Member> findMemberDetails(String clubCode) {
		return clubMemberRepository.findMemberDetails(clubCode); 
	}

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

	// 클럽가입한 회원수 구하기
	@Override
	public Map<String, Integer> membercount() {
        List<Object[]> results = clubMemberRepository.countByClubCode();
        
        return results.stream()
            .collect(Collectors.toMap(
                r -> (String) r[0],  // 클럽코드
                r -> ((Long) r[1]).intValue()  // 회원수
            ));
		
		/* return clubMemberRepository.countByClubCode(clubCode).orElse(0); */
	}

	@Override
	public List<ClubDTO> clubListWithMemID(String username) {
		List<Club> myClubList = clubRepository.getClubListWithMemID(username);

		List<ClubDTO> dtoClub = new ArrayList<>();
		myClubList.forEach(club -> {
			ClubDTO dto = ClubDTO.builder().clubCode(club.getClubCode()).clubIntroduction(club.getClubIntroduction()).clubName(club.getClubName())
						.clubImage1(club.getClubImage1()).memberCount(club.getMembers().size()).build();
			dtoClub.add(dto);
		});

		return dtoClub;
	}

	@Override
	public boolean checkClubOwner(String username) {
		long clubCount = clubRepository.countByMemId(username);

		boolean isClubOwner = false;
		if(clubCount > 0) {
			isClubOwner = true;
		}
		return isClubOwner;
	}

	@Override
	public List<ClubDTO> ownerClubListWithMemId(String username) {
		List<Club> ownerClubList = clubRepository.findByMemId(username);

		List<ClubDTO> resClubDTOs = new ArrayList();
		ownerClubList.forEach(club -> {
			ClubDTO dto = ClubDTO.builder().clubCode(club.getClubCode()).clubName(club.getClubName()).build();
			resClubDTOs.add(dto);
		});
		return resClubDTOs;
	}

	@Override
	public List<ClubReservationDTO> getClubResList(String clubCode) {
		List<ClubReservationInterface> clubReservationDTOs = reservationRepository.getClubResList(clubCode);
		log.info("do getClubResList in serviceImpl");
		log.info(clubReservationDTOs);

		List<ClubReservationDTO> dtos = new ArrayList<>();
		for (ClubReservationInterface clubReservationInterface : clubReservationDTOs) {
			ClubReservationDTO dto = ClubReservationDTO.builder().ReservationProgress(clubReservationInterface.getReservationProgress())
									.clubCode(clubReservationInterface.getClubCode()).count(clubReservationInterface.getCount())
									.facilityName(clubReservationInterface.getFacilityName()).nowMemCount(clubReservationInterface.getNowMemCount())
									.reservationCode(clubReservationInterface.getReservationCode()).reservationStartTime(clubReservationInterface.getReservationStartTime())
									.reservationDate(clubReservationInterface.getReservationDate()).build();
			dtos.add(dto);
		}

		return dtos;
	}

	@Override
	public String addClubResMember(String reservationCode, String clubCode, String memId) {
		Optional<Reservation> resOptional = reservationRepository.findById(reservationCode);
		Reservation reservation = resOptional.orElseThrow();
		ClubReservationMemberKeyClass keyClass = new ClubReservationMemberKeyClass();
		keyClass.setReservationCode(reservationCode);
		keyClass.setClubCode(clubCode);
		keyClass.setMemId(memId);

		Optional<Reservation_Member_List> optional = clubReservationMemberRepository.findById(keyClass);
		if(!optional.isEmpty()) {
			return "exist";
		}

		Date reservationDate = reservation.getReservationDate();
		LocalTime reservationTime = reservation.getReservationStartTime();

		Reservation_Member_List reservation_Member_List = Reservation_Member_List.builder().reservationCode(reservationCode)
														.clubCode(clubCode).memId(memId).reservationTime(reservationTime)
														.reservationDate(reservationDate).build();
		Reservation_Member_List result = clubReservationMemberRepository.save(reservation_Member_List);
		if(result != null) {
			return "success";
		}

		return "fail";
	}

	@Override
	public List<ClubBoardDTO> getBoardListByMemID(String username) {
		
		List<Club_Board> list = clubBoardRepository.findTop3ByMemIDOrderByMODIFYDATEDesc(username);

		List<ClubBoardDTO> dtos = new ArrayList<>();

		if(list.size() > 0) {
			for (Club_Board clubBoard : list) {
				ClubBoardDTO dto = entityToDTO(clubBoard);
				dtos.add(dto);
			}
		}

		return dtos;
	}









}
