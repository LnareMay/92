package com.lec.packages.controllers;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lec.packages.domain.Reservation;
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
}
