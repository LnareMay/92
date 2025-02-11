package com.lec.packages.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.lec.packages.domain.Reservation;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.dto.ReservationDTO;
import com.lec.packages.repository.ReservationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {
	
	private final ModelMapper modelMapper;
	private final ReservationRepository reservationRepository;
	
		//로그인 한 유저가 등록한 시설(facilityCode)의 예약내역 
//		@Override	
//		public PageResponseDTO<ReservationDTO> getReservationByFacilityCode(String facilityCode,PageRequestDTO pageRequestDTO) {
//			
//			Pageable pageable = pageRequestDTO.getPageable("reservationCode");
//			Page<Reservation> result = reservationRepository.findByFacilityCode(facilityCode,pageable);
//			
//			List<ReservationDTO> dtoList = result.getContent()
//												 .stream()
//												 .map(reservation -> modelMapper.map(reservation, ReservationDTO.class))
//												 .collect(Collectors.toList());
//		
//			return PageResponseDTO.<ReservationDTO>withAll()
//								  .pageRequestDTO(pageRequestDTO)
//								  .dtoList(dtoList)
//								  .total(result.getTotalPages())
//								  .build();
//		}


	    
	//예약코드로 시설 예약 정보 불러오기
	@Override
	public ReservationDTO getReservationByCode(String reservationCode) {
		
		Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
				.orElseThrow(()->new IllegalArgumentException("시설을 찾을 수 없습니다: "+reservationCode));
		
		return modelMapper.map(reservation, ReservationDTO.class);
		
	}


	//로그인 한 유저의 예약 승인 된 시설 만 노출
	@Override
	public List<ReservationDTO> getConfirmedReservationsForUser(String memId) {
		
		 List<Reservation> reservations = reservationRepository.findConfirmedReservationsByMemId(memId);
	    
	    return reservations.stream()
				           .map(reservation -> modelMapper.map(reservation, ReservationDTO.class))
				           .collect(Collectors.toList());
	    
	}


	@Override
	public Optional<Reservation> getFacilityNameByCode(String reservationCode) {
		return reservationRepository.findByReservationCode(reservationCode);
	}

	
 	//로그인 한 유저가 등록한 시설의 모든 예약 불러오기
    @Override
    public PageResponseDTO<ReservationDTO> getAllReservationsForUser(String memId, PageRequestDTO pageRequestDTO) {
       
	  Pageable pageable = pageRequestDTO.getPageable("CREATEDATE");	//정렬기준 전달 
      Page<Reservation> result = reservationRepository.findAllReservationsWithUser(memId, pageable);
       
      List<ReservationDTO> dtoList = result.getContent()
		        			               .stream()
					                       .map(reservation -> modelMapper.map(reservation, ReservationDTO.class))
							               .collect(Collectors.toList());
    
        return PageResponseDTO.<ReservationDTO>withAll()
				              .pageRequestDTO(pageRequestDTO)
				              .dtoList(dtoList)
				              .total(result.getTotalPages())
				              .build();
    }
    
	@Override
	public List<ReservationDTO> getAllReservationsForUser(String memId) {
		
		 List<Reservation> reservations = reservationRepository.findAllReservationsWithUserToList(memId);
		    
		    return reservations.stream()
					           .map(reservation -> modelMapper.map(reservation, ReservationDTO.class))
					           .collect(Collectors.toList());
	}

    //로그인 한 유저가 등록한 시설들의 예약완료 상태 예약만 불러오기
	@Override
	public PageResponseDTO<ReservationDTO> getConfirmReservationsForUser(String memId, PageRequestDTO pageRequestDTO) {
		
		 Pageable pageable = pageRequestDTO.getPageable("CREATEDATE");	//정렬기준 전달 
	     Page<Reservation> result = reservationRepository.findConfirmReservationsWithUser(memId, pageable);
	       
	     List<ReservationDTO> dtoList = result.getContent()
			        			               .stream()
						                       .map(reservation -> modelMapper.map(reservation, ReservationDTO.class))
								               .collect(Collectors.toList());
	    
	        return PageResponseDTO.<ReservationDTO>withAll()
					              .pageRequestDTO(pageRequestDTO)
					              .dtoList(dtoList)
					              .total(result.getTotalPages())
					              .build();
	 
	}

	//로그인 한 유저가 등록한 시설들의 예약취소 상태 예약만 불러오기
	@Override
	public PageResponseDTO<ReservationDTO> getRefuseReservationsForUser(String memId, PageRequestDTO pageRequestDTO) {
		
		Pageable pageable = pageRequestDTO.getPageable("CREATEDATE");	//정렬기준 전달 
	    Page<Reservation> result = reservationRepository.findRefuseReservationsWithUser(memId, pageable);
	       
	    List<ReservationDTO> dtoList = result.getContent()
			        			               .stream()
						                       .map(reservation -> modelMapper.map(reservation, ReservationDTO.class))
								               .collect(Collectors.toList());
	    
	     return PageResponseDTO.<ReservationDTO>withAll()
					              .pageRequestDTO(pageRequestDTO)
					              .dtoList(dtoList)
					              .total(result.getTotalPages())
					              .build();
	 
	}


	//로그인 한 유저가 등록한 시설들의 예약진행중 상태 예약만 불러오기
	@Override
	public PageResponseDTO<ReservationDTO> getInprogressReservationsForUser(String memId,PageRequestDTO pageRequestDTO) {
		Pageable pageable = pageRequestDTO.getPageable("CREATEDATE");	//정렬기준 전달 
	    Page<Reservation> result = reservationRepository.findInprogressReservationsWithUser(memId, pageable);
	       
	    List<ReservationDTO> dtoList = result.getContent()
			        			               .stream()
						                       .map(reservation -> modelMapper.map(reservation, ReservationDTO.class))
								               .collect(Collectors.toList());
	    
	     return PageResponseDTO.<ReservationDTO>withAll()
					              .pageRequestDTO(pageRequestDTO)
					              .dtoList(dtoList)
					              .total(result.getTotalPages())
					              .build();
	 
	}






	
	
	
}
