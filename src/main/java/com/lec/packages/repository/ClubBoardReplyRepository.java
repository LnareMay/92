package com.lec.packages.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lec.packages.domain.Club_Board_Reply;

public interface ClubBoardReplyRepository extends JpaRepository<Club_Board_Reply, Integer>{
    @Query(value = "select * from club_board_reply sbr where sbr.clubBoard.clubCode = :clubCode and sbr.clubBoard.boardNo = :boardNo order by sbr.replyNo desc limit 1", nativeQuery = true)
    Optional<Club_Board_Reply> findReplyNoByKey(@Param("clubCode") String clubCode, @Param("boardNo") int boardNO);
}
