package com.lec.packages.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lec.packages.domain.Member;
import com.lec.packages.domain.Reservation;
import com.lec.packages.domain.TransferHistory;

public interface TransferHistoryRepository extends JpaRepository<TransferHistory, String> {

    @Query("SELECT th FROM TransferHistory th WHERE th.payCode = :payCode")
    Optional<TransferHistory> findByPayCode(@Param("payCode") String payCode);

    @Query("SELECT th FROM TransferHistory th JOIN FETCH th.senderId JOIN FETCH th.receiverId WHERE th.senderId.memId = :memId")
    List<TransferHistory> findByMemId(@Param("memId") String memId);

}

