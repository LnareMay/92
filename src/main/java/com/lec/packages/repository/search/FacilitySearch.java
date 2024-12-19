package com.lec.admin.repository.search;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lec.admin.domain.facility;

public interface FacilitySearch {
	
	Page<facility> searchAll(String[] types,String keyword,Pageable pageable);
	Page<facility> searchAllImpl(String[] types,String keyword,Pageable pageable);
	
}
