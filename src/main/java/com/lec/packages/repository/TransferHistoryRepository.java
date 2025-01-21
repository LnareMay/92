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

    @Query("SELECT th FROM TransferHistory th WHERE th.transferDate = :transferDate AND th.senderId.memId = :senderId")
    Optional<TransferHistory> findByTransferDateAndSenderId(@Param("transferDate") LocalDateTime transferDate,
                                                            @Param("senderId") String senderId);
}

