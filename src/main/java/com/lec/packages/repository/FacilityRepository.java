package com.lec.packages.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.lec.packages.domain.facility;
import com.lec.packages.repository.search.FacilitySearch;



public interface FacilityRepository extends JpaRepository<facility,String> , FacilitySearch{

}
