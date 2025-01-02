package com.lec.packages.domain.primaryKeyClasses;

import java.io.Serializable;

import com.lec.packages.domain.Club_Board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ClubBoardReplyKeyClass implements Serializable{
    private Club_Board clubBoard;
    private int replyNo;
}
