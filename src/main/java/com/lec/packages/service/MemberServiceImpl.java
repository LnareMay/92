package com.lec.packages.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lec.packages.domain.Member;
import com.lec.packages.domain.MemberRole;
import com.lec.packages.domain.exercise_code_table;
import com.lec.packages.dto.MemberJoinDTO;
import com.lec.packages.dto.MemberSecurityDTO;
import com.lec.packages.repository.ExerciseRepository;
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
	private final ExerciseRepository exerciseRepository;
//	private final Member member;
	
	@Override
	public void join(MemberJoinDTO memberJoinDTO, String storedFileName) /*throws MidExistException*/ {
		String mid = memberJoinDTO.getMemId();
//		boolean exist = memberRepository.existsById(mid);
//		if(exist) {
//			throw new MidExistException("동일한 아이디가 존재합니다.");
//		}
		
		Member member = modelMapper.map(memberJoinDTO, Member.class);
		member.changePassword(passwordEncoder.encode(memberJoinDTO.getMemPw()));
		
		 // MEM_ISMANAGER 값이 true(1)인 경우 ROLE_ADMIN 추가
	    if (memberJoinDTO.isMemIsmanager()) {
	        member.addRole(MemberRole.ADMIN);
	    } else {
	        member.addRole(MemberRole.USER);
	    }

		
	    member.setMemPicture(storedFileName);
	    
		// log.info("========>" + member + "[" + member.getRoleSet() + "]");
		
		memberRepository.save(member);
	}

	@Override
	public boolean isDuplicateId(String memId) {
	    return memberRepository.existsById(memId); // 존재하면 true 반환
	}

	@Override
	public void modify(MemberJoinDTO memberJoinDTO, String storedFileName) {
	    // 1. 기존 회원 정보 가져오기
	    Optional<Member> existingMember = memberRepository.findById(memberJoinDTO.getMemId());

	    if (existingMember.isPresent()) {
	        Member member = existingMember.get(); // 기존 회원 정보 가져오기
	        
	        // `memExercise` 영속 상태 설정
	        if (memberJoinDTO.getMemExercise() != null) {
	            exercise_code_table exercise = exerciseRepository.findById(memberJoinDTO.getMemExercise().getEXERCISE_CODE())
	                    .orElseThrow(() -> new IllegalArgumentException("Exercise not found with code: " + memberJoinDTO.getMemExercise().getEXERCISE_CODE()));
	            member.setMemExercise(exercise);
	        } else {
	            member.setMemExercise(null); // 선택되지 않은 경우 null로 설정
	        }

	        // `memClub` 영속 상태 설정
	        if (memberJoinDTO.getMemClub() != null) {
	            exercise_code_table club = exerciseRepository.findById(memberJoinDTO.getMemClub().getEXERCISE_CODE())
	                    .orElseThrow(() -> new IllegalArgumentException("Club not found with code: " + memberJoinDTO.getMemClub().getEXERCISE_CODE()));
	            member.setMemClub(club);
	        } else {
	            member.setMemClub(null); // 선택되지 않은 경우 null로 설정
	        }

	        // 기타 정보 업데이트
	        member.setMemName(memberJoinDTO.getMemName());
	        member.setMemNickname(memberJoinDTO.getMemNickname());
	        member.setMemPicture(storedFileName);
	        member.setMemIntroduction(memberJoinDTO.getMemIntroduction());
	        member.setMemTell(memberJoinDTO.getMemTell());
	        member.setMemEmail(memberJoinDTO.getMemEmail());
	        member.setMemAddress(memberJoinDTO.getMemAddress());
	        member.setMemAddressDetail(memberJoinDTO.getMemAddressDetail());
	        member.setMemZipcode(memberJoinDTO.getMemZipcode());

	        // 저장
	        memberRepository.save(member);
	    } else {
	        throw new IllegalArgumentException("Member not found with ID: " + memberJoinDTO.getMemId());
	    }
	}



	@Override
	public void remove(String username) {
	    // 사용자 조회
	    Member member = memberRepository.findById(username)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

	    // DELETE_FLAG를 true(1)로 설정
	    member.setDeleteFlag(true);

	    // 변경된 내용을 저장
	    memberRepository.save(member);
	}





//	public void saveMemberFile(MemberSecurityDTO memberSecurityDTO, String fileName) {
//		// 업로드된 파일명을 MemberJoinDTO에 설정
//		memberSecurityDTO.setMEM_PICTURE(fileName);
//
//		// DTO를 엔티티로 변환 후 DB에 저장 (변환 작업은 필요시 추가)
//		memberRepository.save(member); // `toEntity`는 필요시 구현
//	}

	
}