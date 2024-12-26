package com.lec.packages.service;

import com.lec.packages.dto.MemberJoinDTO;

public interface MemberService {
	// 회원가입에서 해당아이디가 존재할 경우에는
	// MemberRepository.save()가 아니라 MemberRepository.update()로 처리해야 한다.
	// 그래서, 아이디가 존재하면 예외처리를 해야 한다.
	
//	public static class MidExistException extends Exception {
//	    public MidExistException(String message) {
//	        super(message);
//	    }
//	}

//	void join(MemberJoinDTO memberJoinDTO) throws MidExistException;
	
	void join(MemberJoinDTO memberJoinDTO);

	boolean isDuplicateId(String memId);

	void modify(MemberJoinDTO memberJoinDTO);
}
