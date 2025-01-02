package com.lec.packages.repository.search;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lec.packages.domain.Facility;



public interface FacilitySearch {
	
	Page<Facility> searchByUser(String userId, Pageable pageable);
	Page<Facility> searchAll(String[] types,String keyword,Pageable pageable);
	Page<Facility> searchAllImpl(String[] types,String[] keyword,Pageable pageable);

	
}
