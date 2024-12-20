package com.lec.packages.domain;

import com.lec.packages.domain.primaryKeyClasses.ClubBoardKeyClass;

import groovy.transform.ToString;
import groovy.transform.builder.Builder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ClubBoardKeyClass.class)
@ToString
public class Club_Board extends BaseEntity{
    @Id
    @JoinColumn(name = "CLUB_CODE")
    @Column(nullable = false, name = "CLUB_CODE", length = 10)
    private String clubCode;

    @Id
    @Column(nullable = false, name = "BOARD_NO")
    private int boardNo;

    @Column(name = "BOARD_TYPE", length = 20)
    private String boardType;

    @Column(name = "BOARD_TEXT", columnDefinition = "TEXT")
    private String boardText;

    @JoinColumn(name = "MEM_ID")
    @Column(name = "MEM_ID", length = 10)
    private String memID;

    @Column(name = "DELETE_FLAG")
    private Boolean DELETE_FLAG;

    public void change(String type, String text){
        this.boardType = type;
        this.boardText = text;
    }
}
