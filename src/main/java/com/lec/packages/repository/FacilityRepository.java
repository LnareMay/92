package com.lec.packages.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lec.packages.domain.Facility;
import com.lec.packages.repository.search.FacilitySearch;

public interface FacilityRepository extends JpaRepository<Facility,String> , FacilitySearch{

	 Optional <Facility> findByFacilityCode(String facilityCode); 



}
