package com.lec.packages.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.lec.packages.domain.Facility;
import com.lec.packages.domain.Member;
import com.lec.packages.domain.MemberRole;
import com.lec.packages.domain.Reservation;
import com.lec.packages.domain.TransferHistory;
import com.lec.packages.dto.FacilityDTO;
import com.lec.packages.dto.MemberJoinDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.dto.ReservationDTO;
import com.lec.packages.dto.TransferHistoryDTO;
import com.lec.packages.repository.FacilityRepository;
import com.lec.packages.repository.MemberRepository;
import com.lec.packages.repository.ReservationRepository;
import com.lec.packages.repository.TransferHistoryRepository;
import com.lec.packages.util.RandomStringGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class FacilityServiceImpl implements FacilityService{


	private final ModelMapper modelMapper;

	private final FacilityRepository facilityRepository;

	private final MemberRepository memberRepository;
	
	private final ReservationRepository reservationRepository;
	
	private final TransferHistoryRepository transferHistoryRepository;
	

	//시설 등록
	@Transactional
	@Override
	public String register(FacilityDTO facilityDTO) {
		
		Facility facility = modelMapper.map(facilityDTO, Facility.class);
		//고유한 FacilityCode 생성
        String uniqueFacilityCode = RandomStringGenerator.generateRandomString(8,facilityRepository); // 8자리 랜덤 문자열
        facility.setFacilityCode("FACIL_"+uniqueFacilityCode);
		
        // 파일 경로 설정 (DTO에서 이미 설정된 경로를 사용)
        facility.setFacilityImage1(facilityDTO.getFacilityImage1());
        facility.setFacilityImage2(facilityDTO.getFacilityImage2());
        facility.setFacilityImage3(facilityDTO.getFacilityImage3());
        facility.setFacilityImage4(facilityDTO.getFacilityImage4());

        //엔티티를 데이터베이스에 저장
        facilityRepository.save(facility);
        
        return facility.getFacilityCode();

	}

	
	//유저별 시설 목록 보기
	@Override
	public PageResponseDTO<FacilityDTO> listByUser(String userId, PageRequestDTO pageRequestDTO){
		
		Pageable pageable = pageRequestDTO.getPageable("facilityCode");
		Page<Facility> result = facilityRepository.searchByUser(userId, pageable);
		
		List<FacilityDTO> dtoList = result.getContent()
										  .stream()
										  .map(facility->modelMapper.map(facility, FacilityDTO.class))
										  .collect(Collectors.toList());
		
		return PageResponseDTO.<FacilityDTO>withAll()
				   			  .pageRequestDTO(pageRequestDTO)
				   			  .dtoList(dtoList)
				   			  .total(result.getTotalPages())
				   			  .build();
	}
	
	//시설 목록
	/*
	@Override
	public PageResponseDTO<FacilityDTO> list(PageRequestDTO pageRequestDTO) {
		
		String[] types = pageRequestDTO.getTypes();
		String[] keywords = pageRequestDTO.getKeywords();
		Pageable pageable = pageRequestDTO.getPageable("facilityCode");
		
				
		
		Page<Facility> result = facilityRepository.searchAllImpl(types, keywords, pageable);

		
		List<FacilityDTO> dtoList = result.getContent()
										  .stream()
										  .distinct() // 중복 제거
										  .map(facility -> modelMapper.map(facility, FacilityDTO.class))
										  .collect(Collectors.toList());
	
		
		return PageResponseDTO.<FacilityDTO>withAll()
				.pageRequestDTO(pageRequestDTO)
				.dtoList(dtoList)
				.total(result.getTotalPages())
				.build();
	}
	*/
	
	// 시설목록 새로만듬
	@Override
	public PageResponseDTO<FacilityDTO> listAllFacility(PageRequestDTO pageRequestDTO
				,String facilityAddress, String exerciseCode, Boolean facilityIsOnlyClub) {		
		Pageable pageable = pageRequestDTO.getPageable("facilityCode");
		
        // 검색 필터링
        String[] types = {"a", "e", "c", "ae","aec"};
        String[] keywords = new String[3];
        keywords[0] = (facilityAddress != null) ? facilityAddress : "ALL";
        keywords[1] = (exerciseCode != null) ? exerciseCode : "ALL";
        keywords[2] = (facilityIsOnlyClub == null) ? null : (facilityIsOnlyClub ? "true" : "false");
        
		Page<Facility> result = facilityRepository.searchAllImpl(types, keywords, pageable);
		
		List<FacilityDTO> dtoList = result.getContent()
				  .stream()
				  .distinct() // 중복 제거
				  .map(facility -> modelMapper.map(facility, FacilityDTO.class))
				  .collect(Collectors.toList());
		
		log.info("=== Facility Keywords==== : {}, {}, {}", keywords[0], keywords[1], keywords[2]); 
		
		return PageResponseDTO.<FacilityDTO>withAll()
				.pageRequestDTO(pageRequestDTO)
				.dtoList(dtoList)
				.total((int)result.getTotalElements())
				.build();
	}




	//시설 상세보기 
	@Override
	public FacilityDTO getFacilityByCode(String facilityCode) {
		
		Facility facility = 
				facilityRepository.findByFacilityCode(facilityCode)
													.orElseThrow(()->new IllegalArgumentException("시설을 찾을 수 없습니다: "+facilityCode));
		
		 // ModelMapper를 사용하여 Facility -> FacilityDTO 변환
		return modelMapper.map(facility, FacilityDTO.class);
	}

	@Override
	public void modify(FacilityDTO facilityDTO) {
		
		Optional<Facility> result = facilityRepository.findByFacilityCode(facilityDTO.getFacilityCode());
		Facility facility = result.orElseThrow();
		 
	    if (facilityDTO.getFacilityImage1() == null) facilityDTO.setFacilityImage1(facility.getFacilityImage1());
	    if (facilityDTO.getFacilityImage2() == null) facilityDTO.setFacilityImage2(facility.getFacilityImage2());
	    if (facilityDTO.getFacilityImage3() == null) facilityDTO.setFacilityImage3(facility.getFacilityImage3());
	    if (facilityDTO.getFacilityImage4() == null) facilityDTO.setFacilityImage4(facility.getFacilityImage4());

		
		facility.modifyFacility(facilityDTO.getFacilityName()
							   ,facilityDTO.getFacilityDescription()
							   ,facilityDTO.isFacilityIsOnlyClub()
							   ,facilityDTO.getPrice()
							   ,facilityDTO.getFacilityStartTime()
							   ,facilityDTO.getFacilityEndTime()
							   ,facilityDTO.getExerciseCode()
							   ,facilityDTO.getFacilityImage1()
							   ,facilityDTO.getFacilityImage2()
							   ,facilityDTO.getFacilityImage3()
							   ,facilityDTO.getFacilityImage4());
		
		facilityRepository.save(facility);
		
		
	}
	@Override
	public void bookByMember(TransferHistoryDTO transferHistoryDTO, ReservationDTO reservationDTO, BigDecimal memMoney) {
	    // Step 1: 예약 정보를 검증
	    if (reservationDTO.getReservationStartTime().isAfter(reservationDTO.getReservationEndTime())) {
	        throw new IllegalArgumentException("예약 시작 시간이 종료 시간보다 늦을 수 없습니다.");
	    }

	    // Step 2: 예약 시간 차이를 계산하여 총 가격 설정
	    long hours = java.time.Duration.between(
	            reservationDTO.getReservationStartTime(),
	            reservationDTO.getReservationEndTime()
	    ).toHours();
	    BigDecimal totalPrice = BigDecimal.valueOf(hours).multiply(reservationDTO.getPrice());

	    // Step 3: ReservationCode 생성
	    String reservationCode = "" + System.currentTimeMillis();
	    reservationDTO.setReservationCode(reservationCode);
	    transferHistoryDTO.setTransferCode(reservationCode);

	    // Step 4: ReservationDTO를 Reservation 엔티티로 변환
		Reservation reservation;
		if(reservationDTO.getClubCode() == null || reservationDTO.getClubCode().equalsIgnoreCase("")) {
			reservation = Reservation.builder()
					.reservationCode(reservationCode) // 고유 예약 코드 설정
					.facilityCode(reservationDTO.getFacilityCode())
					.facilityName(reservationDTO.getFacilityName())
					.memId(reservationDTO.getMemId())
					.reservationStartTime(reservationDTO.getReservationStartTime())
					.reservationEndTime(reservationDTO.getReservationEndTime())
					.reservationDate(reservationDTO.getReservationDate())
					.count(reservationDTO.getCount())
					.price(totalPrice)
					.reservationProgress("예약진행중") // 초기 상태 설정
					.deleteFlag(false) // 초기 상태 설정
					.build();
		} else {
			reservation = Reservation.builder()
					.reservationCode(reservationCode) // 고유 예약 코드 설정
					.facilityCode(reservationDTO.getFacilityCode())
					.facilityName(reservationDTO.getFacilityName())
					.memId(reservationDTO.getMemId())
					.reservationStartTime(reservationDTO.getReservationStartTime())
					.reservationEndTime(reservationDTO.getReservationEndTime())
					.reservationDate(reservationDTO.getReservationDate())
					.count(reservationDTO.getCount())
					.price(totalPrice)
					.reservationProgress("예약진행중") // 초기 상태 설정
					.deleteFlag(false) // 초기 상태 설정
					.clubCode(reservationDTO.getClubCode())
					.build();
		}

		TransferHistory transferHistory;
		if(reservationDTO.getClubCode() == null || reservationDTO.getClubCode().equalsIgnoreCase("")) {
			transferHistory = TransferHistory.builder()
					.transferCode(reservationCode)
					.amount(totalPrice)
					.memo(transferHistoryDTO.getMemo())
					.status("송금성공")
					.transferDate(LocalDateTime.now())
					.receiverId(transferHistoryDTO.getReceiverId())
					.senderId(transferHistoryDTO.getSenderId())
					.build();
		} else {
			transferHistory = TransferHistory.builder()
					.transferCode(reservationCode)
					.amount(totalPrice)
					.memo(transferHistoryDTO.getMemo())
					.status("송금성공")
					.transferDate(LocalDateTime.now())
					.receiverId(transferHistoryDTO.getReceiverId())
					.senderId(transferHistoryDTO.getSenderId())
					.clubCode(reservationDTO.getClubCode())
					.build();
		}

	    // Step 5: 데이터베이스에 저장
	    reservationRepository.save(reservation);
	    transferHistoryRepository.save(transferHistory);

	    // Step 6: senderId(예약자)의 memMoney 업데이트
	    Member sender = memberRepository.findById(reservationDTO.getMemId())
	            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
	    sender.setMemMoney(memMoney);
	    memberRepository.save(sender);

	    // Step 7: receiverId의 memMoney 업데이트
	    Member receiverMember = transferHistoryDTO.getReceiverId(); // Member 객체
	    if (receiverMember == null || receiverMember.getMemId() == null) {
	        throw new IllegalArgumentException("수신자 정보가 유효하지 않습니다.");
	    }

	    Member receiver = memberRepository.findById(receiverMember.getMemId())
	            .orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다. ID: " + receiverMember.getMemId()));

	    BigDecimal updatedReceiverMoney = receiver.getMemMoney().add(totalPrice); // 기존 금액 + totalPrice
	    receiver.setMemMoney(updatedReceiverMoney);
	    memberRepository.save(receiver);

	    // 로그 출력 (선택 사항)
	    log.info("시설 예약이 완료되었습니다: {}", reservation);
	    log.info("송금 내역: {}", transferHistory);
	    log.info("수신자 업데이트된 잔액: {}", updatedReceiverMoney);
	}

	@Override
	public List<ReservationDTO> getReservationsByFacilityCode(String facilityCode) {
	    // 1. facilityCode에 해당하는 Reservation 엔티티 목록 조회
	    List<Reservation> reservations = reservationRepository.findByFacilityCode(facilityCode);

	    // 2. ModelMapper를 사용해 변환
	    return reservations.stream()
	            .map(reservation -> modelMapper.map(reservation, ReservationDTO.class))
	            .collect(Collectors.toList());
	}






}