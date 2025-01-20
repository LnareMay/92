package com.lec.packages.service;

import java.util.List;
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
	
	//유저별 예약 목록 보기
		@Override
		public PageResponseDTO<ReservationDTO> ReservationlistByUser(String memId, PageRequestDTO pageRequestDTO) {
		    Pageable pageable = pageRequestDTO.getPageable("reservationCode");
		    Page<Reservation> result = reservationRepository.searchByMemId(memId, pageable); // pageable 파라미터 추가
	
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
