package com.lec.packages.domain.primaryKeyClasses;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClubBoardKeyClass implements Serializable{
    private String clubCode;
    private int boardNo;
}
