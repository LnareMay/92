package com.lec.packages.service;




import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.lec.packages.domain.Facility;
import com.lec.packages.dto.FacilityDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.repository.FacilityRepository;
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
		facility.modifyFacility(facilityDTO.getFacilityName()
							   ,facilityDTO.getFacilityDescription()
							   ,facilityDTO.isFacilityIsOnlyClub()
							   ,facilityDTO.getPrice()
							   ,facilityDTO.getFacilityStartTime()
							   ,facilityDTO.getFacilityEndTime());
		
		facilityRepository.save(facility);
		
		
	}





}