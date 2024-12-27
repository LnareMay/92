package com.lec.packages.service;



import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.lec.packages.domain.Facility;
import com.lec.packages.dto.FacilityDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.repository.FacilityRepository;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class FacilityServiceImpl implements FacilityService{

	private final ModelMapper modelMapper;
	private final FacilityRepository facilityRepository;
	
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
	@Override
	public PageResponseDTO<FacilityDTO> list(PageRequestDTO pageRequestDTO) {
		
		String[] types = pageRequestDTO.getTypes();
		String keyword = pageRequestDTO.getKeyword();
		Pageable pageable = pageRequestDTO.getPageable("facilityCode");
		
		Page<Facility> result = facilityRepository.searchAllImpl(types, keyword, pageable);
		List<FacilityDTO> dtoList = result.getContent()
										  .stream()
										  .map(facility -> modelMapper.map(facility, FacilityDTO.class))
										  .collect(Collectors.toList());
		
		return PageResponseDTO.<FacilityDTO>withAll()
				.pageRequestDTO(pageRequestDTO)
				.dtoList(dtoList)
				.total((int)result.getTotalElements())
				.build();
	}


	//시설 등록
	@Override
	public String register(FacilityDTO facilityDTO) {
		
		Facility facility = modelMapper.map(facilityDTO, Facility.class);
		String facilityCode = facilityRepository.save(facility).getFacilityCode();
		
		return facilityCode;
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

}
