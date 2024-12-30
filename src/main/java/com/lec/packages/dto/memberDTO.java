package com.lec.packages.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.lec.packages.domain.exercise_code_table;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class memberDTO extends User{

    @NotEmpty
    private String MEM_ID;

    @NotEmpty
    private String MEM_PW;

    private String MEM_NAME;
    private String MEM_NICKNAME;
    private exercise_code_table MEM_EXERCISE;
    private exercise_code_table MEM_CLUB;
    private long MEM_PICTURE;
    private String MEM_INTRODUCTION;
    private boolean MEM_GENDER;
    private String MEM_TELL;
    private String MEM_EMAIL;
    private String MEM_BIRTHDAY;
    private String MEM_ADDRESS;
    private String MEM_ZIPCODE;
    private String MEM_ADDRESS_SET;
    private boolean MEM_ISMANAGER;
    private boolean DELETE_FLAG;


    public memberDTO(String username, String password,
    String name, String nickname, exercise_code_table MEM_EXERCISE,exercise_code_table MEM_CLUB,
    String tell, String email,
    Collection<? extends GrantedAuthority> authorities){
        super(username, password, authorities);

        this.MEM_ID = username;
        this.MEM_PW = password;
        this.MEM_NAME = name;
        this.MEM_NICKNAME = nickname;
        this.MEM_TELL = tell;
        this.MEM_EMAIL = email;
    }
}
