package com.lec.packages.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class club extends BaseEntity{
    // Id => P.K 명시
    // @Column => 컬럼 정보 셋업
    // length => 컬럼 데이터 길이
    // name => 컬럼 이름
    // @JoinColumn => 외래키 명시

    @Id
    @Column(length = 10, name = "CLUB_CODE")
    private String CLUB_CODE;

    @Column(nullable = false, length = 10, name = "CLUB_NAME")
    private String CLUB_NAME;
    
    @JoinColumn(name = "EXERCISE_CODE")
    @Column(length = 20, name = "CLUB_EXERCISE")
    private String CLUB_EXERCISE;

    @Column(nullable = false, columnDefinition = "TEXT", name = "CLUB_INTRODUCTION")
    private String CLUB_INTRODUCTION;

    @Column(length = 15, name = "CLUB_THEME")
    private String CLUB_THEME;

    @Column(name = "CLUB_IMAGE_1", columnDefinition = "LONGBLOB")
    private long CLUB_IMAGE_1;
    
    @Column(name = "CLUB_IMAGE_2", columnDefinition = "LONGBLOB")
    private long CLUB_IMAGE_2;

    @Column(name = "CLUB_IMAGE_3", columnDefinition = "LONGBLOB")
    private long CLUB_IMAGE_3;

    @Column(name = "CLUB_IMAGE_4", columnDefinition = "LONGBLOB")
    private long CLUB_IMAGE_4;
    
    @Column(nullable = false, length = 100, name = "CLUB_ADDRESS")
    private String CLUB_ADDRESS;

    @JoinColumn(name = "MEM_ID")
    @Column(length = 20, name = "MEM_ID")
    private String MEM_ID;

    @Column(name = "CLUB_ISPRIVATE")
    private boolean CLUB_ISPRIVATE;
    @Column(length = 30, name = "CLUB_PW")
    private String CLUB_PW;

    @Column(name = "DELETE_FLAG")
    private boolean DELETE_FLAG;
}
