package com.lec.packages.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lec.packages.domain.Club_Board;
import com.lec.packages.domain.primaryKeyClasses.ClubBoardKeyClass;
import com.lec.packages.repository.search.ClubBoardSearch;

public interface ClubBoardRepository extends JpaRepository<Club_Board, ClubBoardKeyClass>, ClubBoardSearch{

    @Query(value = "select * from club_board cb where cb.CLUB_CODE = :CLUB_CODE order by BOARD_NO desc limit 1", nativeQuery = true)
    Optional<Club_Board> findByClubCode(@Param("CLUB_CODE") String clubCode);

    @EntityGraph(attributePaths = {"images"})
	@Query("select cb from Club_Board cb where cb.clubCode = :clubCode and cb.boardNo = :boardNo")
	Optional<Club_Board> findBoardByImages(@Param("clubCode") String clubCode, @Param("boardNo") int boardNO);

    @EntityGraph(attributePaths = {"images"})
    // @Query("select cb from Club_Board cb where cb.memID =:memId order by cb.MODIFYDATE desc")
    List<Club_Board> findTop3ByMemIDOrderByMODIFYDATEDesc(String memId);
}
