package com.lec.packages.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lec.packages.domain.TransferHistory;

public interface TransferHistoryRepository extends JpaRepository<TransferHistory, String> {

    @Query("SELECT th FROM TransferHistory th WHERE th.payCode = :payCode")
    Optional<TransferHistory> findByPayCode(@Param("payCode") String payCode);

    @Query("SELECT th FROM TransferHistory th JOIN FETCH th.senderId JOIN FETCH th.receiverId WHERE th.senderId.memId = :memId order by th.transferDate desc")
    List<TransferHistory> findByMemId(@Param("memId") String memId);
    
    @Query("SELECT th FROM TransferHistory th JOIN FETCH th.senderId JOIN FETCH th.receiverId WHERE th.receiverId.memId = :memId order by th.transferDate desc")
    List<TransferHistory> findByReceiverMemId(@Param("memId") String memId);

}

