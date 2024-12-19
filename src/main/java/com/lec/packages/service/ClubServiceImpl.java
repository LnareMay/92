package com.lec.packages.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.lec.packages.domain.Club;
import com.lec.packages.dto.ClubDTO;
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
	
	public String create(ClubDTO clubDTO) {
		Club club = modelMapper.map(clubDTO, Club.class);
		String code = clubRepository.save(club).getClubCode();
		return code;		
	}
	
}
