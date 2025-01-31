package com.lec.packages.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    //로그인한 유저가 등록한 시설에서 예약 완료된 시설의 pay_code를 참고하여 transfer_history 테이블에서 송금 내역을 가져오
    @Query("SELECT th FROM TransferHistory th " + "WHERE th.payCode IN (" + " SELECT r.payCode FROM Reservation r " + " WHERE r.facilityCode"
    														    + " IN (" + " SELECT f.facilityCode FROM Facility f WHERE f.memId = :memId" + " ) "
    														    + "AND r.reservationProgress = '예약완료'" + ")")
    Page<TransferHistory> findTransferHistoriesByMemId(@Param("memId") String memId, Pageable pageable);
    

}

