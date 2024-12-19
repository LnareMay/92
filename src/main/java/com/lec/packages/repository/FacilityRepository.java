package com.lec.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lec.admin.domain.facility;
import com.lec.admin.repository.search.FacilitySearch;

public interface FacilityRepository extends JpaRepository<facility,String> , FacilitySearch{

}
