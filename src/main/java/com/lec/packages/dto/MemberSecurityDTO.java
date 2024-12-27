package com.lec.packages.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.lec.packages.domain.Member;

import groovy.transform.builder.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberSecurityDTO extends User {

	// 추가 필드
	private String MEM_ID;
	private String MEM_PW;
	private String MEM_NAME;
	private String MEM_NICKNAME;
	private String MEM_EXERCISE;
	private String MEM_CLUB;
	private String MEM_PICTURE;
	private String MEM_INTRODUCTION;
	private boolean MEM_GENDER;
	private String MEM_TELL;
	private String MEM_EMAIL;
	private String MEM_BIRTHDAY;
	private String MEM_ADDRESS;
	private String MEM_ADDRESS_DETAIL;
	private String MEM_ZIPCODE;
	private String MEM_ADDRESS_SET;
	private boolean MEM_ISMANAGER;
	private boolean DELETE_FLAG;
	private boolean MEM_SOCIAL;

	// 생성자
	public MemberSecurityDTO(String username, String password, String MEM_NAME, String MEM_NICKNAME,
			String MEM_EXERCISE, String MEM_CLUB, String MEM_PICTURE, String MEM_INTRODUCTION, boolean MEM_GENDER,
			String MEM_TELL, String MEM_EMAIL, String MEM_BIRTHDAY, String MEM_ADDRESS,String MEM_ADDRESS_DETAIL, String MEM_ZIPCODE, String MEM_ADDRESS_SET, 
			 boolean MEM_ISMANAGER, boolean DELETE_FLAG,boolean MEM_SOCIAL,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);

		this.MEM_ID = username; // username은 MEM_ID로 설정
		this.MEM_PW = password; // password는 MEM_PW로 설정
		this.MEM_NAME = MEM_NAME;
		this.MEM_NICKNAME = MEM_NICKNAME;
		this.MEM_EXERCISE = MEM_EXERCISE;
		this.MEM_CLUB = MEM_CLUB;
		this.MEM_PICTURE = MEM_PICTURE;
		this.MEM_INTRODUCTION = MEM_INTRODUCTION;
		this.MEM_GENDER = MEM_GENDER;
		this.MEM_TELL = MEM_TELL;
		this.MEM_EMAIL = MEM_EMAIL;
		this.MEM_BIRTHDAY = MEM_BIRTHDAY;
		this.MEM_ADDRESS = MEM_ADDRESS;
		this.MEM_ADDRESS_DETAIL = MEM_ADDRESS_DETAIL;
		this.MEM_ZIPCODE = MEM_ZIPCODE;
		this.MEM_ADDRESS_SET = MEM_ADDRESS_SET;
		this.MEM_ISMANAGER = MEM_ISMANAGER;
		this.DELETE_FLAG = DELETE_FLAG;
		this.MEM_SOCIAL = MEM_SOCIAL;
	}

	
}
