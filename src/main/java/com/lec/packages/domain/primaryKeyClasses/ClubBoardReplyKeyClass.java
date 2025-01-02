package com.lec.packages.domain.primaryKeyClasses;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ClubBoardReplyKeyClass implements Serializable{
    private String clubCode;
    private int boardNo;
    private int replyNo;
}
