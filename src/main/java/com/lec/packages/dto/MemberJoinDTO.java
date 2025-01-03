package com.lec.packages.dto;

import com.lec.packages.domain.exercise_code_table;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class MemberJoinDTO {
	private String memId;
	private String memPw;
	private String memName;
	private String memNickname;
	private exercise_code_table memExercise;
	private exercise_code_table memClub;
	private String memPicture;
	private String memIntroduction;
	private boolean memGender;
	private String memTell;
	private String memEmail;
	private String memBirthday;
	private String memAddress;
	private String memAddressDetail;
	private String memZipcode;
	private String memAddressSet;
	private boolean memIsmanager;
	private boolean deleteFlag;
	private boolean memSocial;

	
	
}



