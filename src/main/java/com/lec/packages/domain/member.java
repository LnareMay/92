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

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class member extends BaseEntity{

    @Id
    @Column(name = "MEM_ID", length = 20)
    private String MEM_ID;

    @Column(name = "MEM_PW", length = 30)
    private String MEM_PW;

    @Column(name = "MEM_NAME", length = 5)
    private String MEM_NAME;

    @Column(name = "MEM_NICKNAME", length = 10)
    private String MEM_NICKNAME;

    @JoinColumn(name = "EXERCISE_CODE")
    @Column(name = "MEM_EXERCISE", length = 15)
    private String MEM_EXERCISE;

    @Column(name = "MEM_PICTURE", columnDefinition = "LONGBLOB")
    private long MEM_PICTURE;

    @Column(name = "MEM_INTRODUCTION", columnDefinition = "TEXT")
    private String MEM_INTRODUCTION;

    @Column(name = "MEM_GENDER")
    private boolean MEM_GENDER;

    @Column(name = "MEM_TELL", length = 11)
    private String MEM_TELL;

    @Column(name = "MEM_EMAIL", length = 30)
    private String MEM_EMAIL;

    @Column(name = "MEM_BIRTHDAY", length = 9)
    private String MEM_BIRTHDAY;

    @Column(name = "MEM_ADDRESS", length = 100)
    private String MEM_ADDRESS;

    @Column(name = "MEM_ZIPCODE", length = 10)
    private String MEM_ZIPCODE;

    @Column(name = "MEM_ADDRESS_SET", length = 100)
    private String MEM_ADDRESS_SET;

    @Column(name = "MEM_ISMANAGER")
    private boolean MEM_ISMANAGER;

    @Column(name = "DELETE_FLAG")
    private boolean DELETE_FLAG;

}
