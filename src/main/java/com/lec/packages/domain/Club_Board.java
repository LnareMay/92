package com.lec.packages.domain;

import com.lec.packages.domain.primaryKeyClasses.ClubBoardKeyClass;

import groovy.transform.ToString;
import groovy.transform.builder.Builder;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Club_Board {
    @Id
    @JoinColumn(name = "CLUB_CODE")
    @Column(nullable = false, name = "CLUB_CODE")
    private ClubBoardKeyClass clubCode;

    @Id
    @Column(nullable = false, name = "BOARD_NO")
    private ClubBoardKeyClass boardNo;
}
