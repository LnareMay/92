package com.lec.packages.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lec.packages.domain.Club_Board;
import com.lec.packages.domain.primaryKeyClasses.ClubBoardKeyClass;

public interface ClubBoardRepository extends JpaRepository<Club_Board, ClubBoardKeyClass>{

    @Query(value = "select * from club_board cb where cb.CLUB_CODE = :CLUB_CODE order by BOARD_NO desc limit 1", nativeQuery = true)
    Optional<Club_Board> findByClubCode(@Param("CLUB_CODE") String clubCode);
}
