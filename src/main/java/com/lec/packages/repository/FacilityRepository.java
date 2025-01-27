package com.lec.packages.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lec.packages.domain.Facility;
import com.lec.packages.domain.Reservation;
import com.lec.packages.repository.search.FacilitySearch;

public interface FacilityRepository extends JpaRepository<Facility,String> , FacilitySearch{
	
    boolean existsByFacilityCode(String facilityCode);
  
    Optional<Facility> findByFacilityCode(@Param("facilityCode") String facilityCode);

    
    List<Facility> findByMemId(String memId);
    
    // 시설검색
    @Query("SELECT f FROM Facility f WHERE f.deleteFlag = false "
    		+ "AND (:address IS NULL OR f.facilityAddress LIKE %:address%) "
    		+ "AND (:exerciseCode IS NULL OR f.exerciseCode LIKE %:exerciseCode%) "
    		+ "AND (:isOnlyClub IS NULL OR f.facilityIsOnlyClub = :isOnlyClub) ")
    Page<Facility> searchAll(@Param("address") String facilityAddress, @Param("exerciseCode") String exerciseCode,
    					 @Param("isOnlyClub") Boolean facilityIsOnlyClub, Pageable pageable);
  

}
