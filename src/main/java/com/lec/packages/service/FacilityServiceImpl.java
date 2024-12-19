package com.lec.admin.service;



import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.lec.admin.domain.facility;
import com.lec.admin.dto.PageRequestDTO;
import com.lec.admin.dto.PageResponseDTO;
import com.lec.admin.dto.facilityDTO;
import com.lec.admin.repository.FacilityRepository;


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
	
	
	@Override
	public PageResponseDTO<facilityDTO> list(PageRequestDTO pageRequestDTO) {
		
		String[] types = pageRequestDTO.getTypes();
		String keyword = pageRequestDTO.getKeyword();
		Pageable pageable = pageRequestDTO.getPageable("FACILITY_CODE");
		
		Page<facility> result = facilityRepository.searchAllImpl(types, keyword, pageable);
		List<facilityDTO> dtoList = result.getContent()
										  .stream()
										  .map(facility -> modelMapper.map(facility, facilityDTO.class))
										  .collect(Collectors.toList());
		
		return PageResponseDTO.<facilityDTO>withAll()
				.pageRequestDTO(pageRequestDTO)
				.dtoList(dtoList)
				.total((int)result.getTotalElements())
				.build();
	}

}
