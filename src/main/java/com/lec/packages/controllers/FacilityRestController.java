package com.lec.packages.controllers;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lec.packages.domain.Reservation;
import com.lec.packages.dto.FacilityDTO;
import com.lec.packages.service.FacilityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/facility")
@RequiredArgsConstructor
public class FacilityRestController {

    @Autowired
    private FacilityService facilityService;

    @GetMapping("/getReservationTimeList/{facilityCode},{reservationDate}")
    public List<Reservation> getReservationTimeList(@PathVariable("facilityCode") String facilityCode, @PathVariable("reservationDate") Date reservationDate) {
        List<Reservation> localTimes = facilityService.getReservationTimeList(facilityCode, reservationDate);

        return localTimes;
    }
    
    // 모든 시설가져오기
    @GetMapping("/search")
    public ResponseEntity<List<FacilityDTO>> getFacilityWithRadius(
    		  @RequestParam(name = "lat") BigDecimal lat
    		, @RequestParam(name = "longt") BigDecimal longt
    		, @RequestParam(name = "radius", defaultValue = "5") double radius) {
    	
    	List<FacilityDTO> facility = facilityService.getFacilityWithRadius(lat, longt, radius);
    	
    	return ResponseEntity.ok(facility);
    }
    
    
}
