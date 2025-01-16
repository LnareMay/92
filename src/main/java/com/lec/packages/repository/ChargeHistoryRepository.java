package com.lec.packages.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lec.packages.domain.ChargeHistory;
import com.lec.packages.domain.Member;
import com.lec.packages.domain.Reservation;
import com.lec.packages.domain.TransferHistory;

public interface ChargeHistoryRepository extends JpaRepository<ChargeHistory, String>{

}
