package com.lec.packages.repository.search;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.lec.packages.domain.Club_Board;
import com.lec.packages.domain.Member;
import com.lec.packages.domain.QClub_Board;
import com.lec.packages.domain.QClub_Board_Reply;
import com.lec.packages.domain.QMember;
import com.lec.packages.dto.ClubBoardAllListDTO;
import com.lec.packages.dto.ClubBoardImageDTO;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ClubBoardSearchImpl extends QuerydslRepositorySupport implements ClubBoardSearch{

    public ClubBoardSearchImpl(){
        super(Club_Board.class); 
    }

    @Override
    public Page<ClubBoardAllListDTO> searchWithAll(String[] types, Pageable pageable, String clubCode) {
        
        QClub_Board club_board = QClub_Board.club_Board;
        QClub_Board_Reply club_board_reply = QClub_Board_Reply.club_Board_Reply;
        QMember member = QMember.member;

        JPQLQuery<Club_Board> clubBoardJpqlQuery = from(club_board);
        if(types == null || types[0].equalsIgnoreCase("ALL")) {
            clubBoardJpqlQuery.where(club_board.clubCode.eq(clubCode), club_board.DELETE_FLAG.isNull());
        } else {
            clubBoardJpqlQuery.where(club_board.clubCode.eq(clubCode), club_board.boardType.eq(types[0]) ,club_board.DELETE_FLAG.isNull());
        }
        clubBoardJpqlQuery.leftJoin(club_board_reply).on(club_board_reply.clubCode.eq(club_board.clubCode)).on(club_board_reply.boardNo.eq(club_board.boardNo), club_board_reply.deleteFlag.isNull());
        clubBoardJpqlQuery.leftJoin(member).on(member.memId.eq(club_board.memID));

        clubBoardJpqlQuery.orderBy(club_board.MODIFYDATE.desc());

        clubBoardJpqlQuery.groupBy(club_board);

        getQuerydsl().applyPagination(pageable, clubBoardJpqlQuery);

        JPQLQuery<Tuple> tupleJpqlQuery = clubBoardJpqlQuery.select(club_board, club_board_reply.countDistinct(), member.memNickname);
        List<Tuple> tupleList = tupleJpqlQuery.fetch();

        List<ClubBoardAllListDTO> dtoList = tupleList.stream().map(tuple -> {
            Club_Board clubBoard = tuple.get(club_board);
            long replyCount = tuple.get(1, Long.class);
            String memNickname = tuple.get(2, String.class);

            LocalDateTime nowDate = LocalDateTime.now();
            String type = "";
            if(clubBoard.getBoardType().equalsIgnoreCase("Notice")) type = "#공지#";
            if(clubBoard.getBoardType().equalsIgnoreCase("FreeBoard")) type = "자유 게시판";
            if(clubBoard.getBoardType().equalsIgnoreCase("Hello")) type = "가입인사";
            if(clubBoard.getBoardType().equalsIgnoreCase("Reviews")) type = "정산&후기";

            ClubBoardAllListDTO dto = ClubBoardAllListDTO.builder()
                                    .boardNo(clubBoard.getBoardNo())
                                    .type(type)
                                    .memId(memNickname)
                                    .boardText(clubBoard.getBoardText())
                                    .modDate(ChronoUnit.DAYS.between(clubBoard.getMODIFYDATE(), nowDate))
                                    .replyCount(Long.valueOf(replyCount).intValue())
                                    .build();
            List<ClubBoardImageDTO> imageDTOs = clubBoard.getImages()
                                                .stream()
                                                .sorted()
                                                .map(boardImage -> ClubBoardImageDTO.builder()
                                                                    .uuid(boardImage.getUuid())
                                                                    .ord(boardImage.getOrd())
                                                                    .boardImage(boardImage.getBoardImage())
                                                                    .build())
                                                .collect(Collectors.toList());
            dto.setBoardImages(imageDTOs);
            // log.info(dto);
            return dto;
        }).collect(Collectors.toList());

        long totalCount = clubBoardJpqlQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);
    }

}
