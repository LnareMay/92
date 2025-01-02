package com.lec.packages.dto;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubBoardReplyDTO {

    private int replyNo;

    private int boardNo;

    private String clubCode;

    private String boardText;

    private String memId;
}
