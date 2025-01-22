package com.lec.packages.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lec.packages.domain.Reservation;

public interface ReservationSearch {

	Page<Reservation> searchByUser(String userId, Pageable pageable);

}
