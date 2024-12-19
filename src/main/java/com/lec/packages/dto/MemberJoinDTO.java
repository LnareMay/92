package com.lec.packages.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class MemberJoinDTO {
	private String MEM_ID;
	private String MEM_PW;
	private String MEM_NAME;
	private String MEM_NICKNAME;
	private String MEM_EXERCISE;
	private String MEM_CLUB;
	private String MEM_PICTURE;
	private String MEM_INTRODUCTION;
	private Boolean MEM_GENDER;
	private String MEM_TELL;
	private String MEM_EMAIL;
	private String MEM_BIRTHDAY;
	private String MEM_ADDRESS;
	private String MEM_ADDRESS_DETAIL;
	private String MEM_ZIPCODE;
	private String MEM_ADDRESS_SET;
	private Boolean MEM_ISMANAGER;
	private Boolean DELETE_FLAG;
	private Boolean MEM_SOCIAL;

}
