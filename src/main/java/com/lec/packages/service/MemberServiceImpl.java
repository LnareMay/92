package com.lec.packages.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lec.packages.domain.Member;
import com.lec.packages.domain.MemberRole;
import com.lec.packages.dto.MemberJoinDTO;
import com.lec.packages.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
	
	private final ModelMapper modelMapper;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public void join(MemberJoinDTO memberJoinDTO) /*throws MidExistException*/ {
		String mid = memberJoinDTO.getMEM_ID();
//		boolean exist = memberRepository.existsById(mid);
//		if(exist) {
//			throw new MidExistException("동일한 아이디가 존재합니다.");
//		}
		
		Member member = modelMapper.map(memberJoinDTO, Member.class);
		member.changePassword(passwordEncoder.encode(memberJoinDTO.getMEM_PW()));
		
		 // MEM_ISMANAGER 값이 true(1)인 경우 ROLE_ADMIN 추가
	    if (memberJoinDTO.isMEM_ISMANAGER()) {
	        member.addRole(MemberRole.ADMIN);
	    } else {
	        member.addRole(MemberRole.USER);
	    }

		
		// log.info("========>" + member + "[" + member.getRoleSet() + "]");
		
		memberRepository.save(member);
	}

	@Override
	public boolean isDuplicateId(String memId) {
	    return memberRepository.existsById(memId); // 존재하면 true 반환
	}

	@Override
	public void modify(MemberJoinDTO memberJoinDTO) {
		// 1. 기존 회원 정보 가져오기
        Optional<Member> existingMember = memberRepository.findById(memberJoinDTO.getMEM_ID());
        
        if (existingMember.isPresent()) {
            // 2. 회원 정보 수정
            Member member =  modelMapper.map(memberJoinDTO, Member.class);
            member.setMEM_ID(memberJoinDTO.getMEM_ID());
            member.setMEM_NAME(memberJoinDTO.getMEM_NAME());
            member.setMEM_NICKNAME(memberJoinDTO.getMEM_NICKNAME());
            member.setMEM_EXERCISE(memberJoinDTO.getMEM_EXERCISE());
            member.setMEM_CLUB(memberJoinDTO.getMEM_CLUB());
            // member.setMEM_PICTURE(memberJoinDTO.getMEM_PICTURE());
            member.setMEM_INTRODUCTION(memberJoinDTO.getMEM_INTRODUCTION());
            member.setMEM_TELL(memberJoinDTO.getMEM_TELL());
            member.setMEM_EMAIL(memberJoinDTO.getMEM_EMAIL());
            member.setMEM_ADDRESS(memberJoinDTO.getMEM_ADDRESS());
            member.setMEM_ADDRESS_DETAIL(memberJoinDTO.getMEM_ADDRESS_DETAIL());
            member.setMEM_ZIPCODE(memberJoinDTO.getMEM_ZIPCODE());
       
            
            // 3. 수정된 정보 저장
            memberRepository.save(member);
        } else {
            throw new IllegalArgumentException("Member not found with ID: " + memberJoinDTO.getMEM_ID());
        }
		
	}


}
