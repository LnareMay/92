package com.lec.packages.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.lec.packages.domain.Club_Board;
import com.lec.packages.domain.QClub_Board;
import com.lec.packages.dto.ClubBoardAllListDTO;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ClubBoardSearchImpl extends QuerydslRepositorySupport implements ClubBoardSearch{

    public ClubBoardSearchImpl(){
        super(Club_Board.class); 
    }

    @Override
    public Page<ClubBoardAllListDTO> searchWithAll(String[] types, Pageable pageable) {
        
        QClub_Board club_board = QClub_Board.club_Board;

        JPQLQuery<Club_Board> clubBoardJpqlQuery = from(club_board);

        getQuerydsl().applyPagination(pageable, clubBoardJpqlQuery);

        // JPQLQuery<Tuple> tupleJpqlQuery = clubBoardJpqlQuery.select(club_board);

        return null;
    }

}
