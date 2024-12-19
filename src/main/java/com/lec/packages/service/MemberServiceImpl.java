package com.lec.packages.service;

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
	public void join(MemberJoinDTO memberJoinDTO) throws MidExistException {
		String mid = memberJoinDTO.getMEM_ID();
		boolean exist = memberRepository.existsById(mid);
		if(exist) {
			throw new MidExistException();
		}
		
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

}
