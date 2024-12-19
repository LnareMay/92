package com.lec.packages.repository.search;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lec.packages.domain.facility;



public interface FacilitySearch {
	
	Page<facility> searchAll(String[] types,String keyword,Pageable pageable);
	Page<facility> searchAllImpl(String[] types,String keyword,Pageable pageable);
	
}
