package com.lec.packages.security;


import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.HashSet;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.lec.packages.domain.Member;
import com.lec.packages.domain.MemberRole;
import com.lec.packages.dto.MemberSecurityDTO;
import com.lec.packages.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


/*
  SSN(Kakao)로그인 연동후 이메일 구하기
  
  DefaultOAuth2UserService를 구현하기 전에 로그인을 하면 전달하는 정보가 UserDetails타입
  이 아니기 때문에 에러가 발생한다. 이를 처리하려면 UserDetailService를 구현하는 것처럼
  OAuth2UserService인터페이스를 구현해야 한다.
  
  이 자체를 구현할 수도 있지만 하위클래스인 DefaultOAuth2UserService를 상속해서 구현하는
  방식이 제일 간단하다.
 */

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService{
	
	/*@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		
		log.info("======> User Request : " + userRequest); 
		log.info("OAuth2 User............................");   
				
		ClientRegistration clientRegistration = userRequest.getClientRegistration();
		String clientName = clientRegistration.getClientName();
		
		log.info("1. client Id = " + clientRegistration.getClientId());
		log.info("2. client Name = " + clientRegistration.getClientName());
		log.info("3. client Secret = " + clientRegistration.getClientSecret());
		
		OAuth2User oAuth2User = super.loadUser(userRequest);
	    Map<String, Object> paramMap = oAuth2User.getAttributes();
	    
	    // Kakao 서비스의 경우 kakao_acount라는 키로 접근하는 정보 중에 email관련 정보가 있다.
	    paramMap.forEach((k,v) -> {
	    	log.info("--------------------------");
	    	log.info(k + "=" + v);
	    });
	    
	    // SSN : google, naver, facebook 등
	    String email = null;
	    switch(clientName) {
	    case "kakao" : 
	    	email = getKakaoEmail(paramMap);
	    	break;
	    case "gmail" :
	    	email = getGmail(paramMap);
	    	break;
	    case "naver" :
	    	email = getNaver(paramMap);
	    	break;
	    case "facebook" :
	    	email = getFacebook(paramMap);
	    	break;
	    }
				 
		return oAuth2User;
	}*/
	
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
	    log.info("======> User Request : {}", userRequest);

	    ClientRegistration clientRegistration = userRequest.getClientRegistration();
	    String clientName = clientRegistration.getClientName();
	    log.info("Client Name: {}", clientName);

	    OAuth2User oAuth2User = super.loadUser(userRequest);
	    Map<String, Object> paramMap = oAuth2User.getAttributes();

	    String email = null;
	    String nickname = null;
	    String picture = null;

	    if ("kakao".equalsIgnoreCase(clientName)) {
	        email = extractKakaoAttribute(paramMap, "email");
	        nickname = extractKakaoAttribute(paramMap, "nickname");
	        picture = extractKakaoAttribute(paramMap, "profile_image_url");
	    }

	    log.info("Extracted Email: {}", email);
	    log.info("Extracted Nickname: {}", nickname);
	    log.info("Extracted Picture: {}", picture);

	    return generateDTO(email, nickname, picture, paramMap);
	}


	
	private MemberSecurityDTO generateDTO(String email, String nickname, String picture, Map<String, Object> paramMap) {
	    Optional<Member> result = memberRepository.findByMemEmail(email);

	    if (result.isEmpty()) {
	        // 신규 사용자 생성
	        Member newMember = createNewMember(email, nickname, picture);
	        memberRepository.save(newMember);

	        MemberSecurityDTO memberSecurityDTO = createMemberSecurityDTO(newMember, paramMap);
	        return memberSecurityDTO;
	    } else {
	        // 기존 사용자 처리
	        Member existingMember = result.get();
	        return createMemberSecurityDTO(existingMember, paramMap);
	    }
	}

	/**
	 * 신규 사용자 생성 메서드
	 */
	private Member createNewMember(String email, String nickname, String picture) {
	    Member member = Member.builder()
	            .memId(email) // 소셜 로그인에서는 email을 ID로 사용
	            .memPw(passwordEncoder.encode(UUID.randomUUID().toString())) // 랜덤 비밀번호 생성
	            .memEmail(email) // 이메일 설정
	            .memNickname(nickname) // 닉네임 설정
	            .memPicture(picture) // 프로필 사진 설정
	            .memSocial(true) // 소셜 로그인 여부
	            .memIsmanager(false) // 기본값: 관리자 아님
	            .deleteFlag(false) // 기본값: 삭제되지 않음
	            .build();

	    // 기본 역할 추가
	 // roleSet 초기화 및 역할 추가
	    if (member.getRoleSet() == null) {
	        member.setRoleSet(new HashSet<>()); // 초기화
	    }
	    member.addRole(MemberRole.USER);

	    return member;
	}


	/**
	 * MemberSecurityDTO 생성 메서드
	 */
	private MemberSecurityDTO createMemberSecurityDTO(Member member, Map<String, Object> paramMap) {
	    return new MemberSecurityDTO(
	    		member.getMemId()
				  , member.getMemPw()
				  , member.getMemName()
				  , member.getMemNickname()
				  , member.getMemExercise()
				  , member.getMemClub()
				  , member.getMemPicture()
				  , member.getMemIntroduction()
				  , member.isMemGender()
				  , member.getMemTell()
				  , member.getMemEmail()
				  , member.getMemBirthday()
				  , member.getMemAddress()
				  , member.getMemAddressDetail()
				  , member.getMemZipcode()
				  , member.getMemAddressSet()
				  , member.isMemIsmanager()
				  , member.isMemSocial()
				  , member.isDeleteFlag()
	            ,member.getRoleSet()
	                    .stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
	                    .collect(Collectors.toList())
	    );
	}


	private String extractKakaoAttribute(Map<String, Object> paramMap, String attributeKey) {
	    Object value = paramMap.get("kakao_account");
	    if (value instanceof LinkedHashMap) {
	        LinkedHashMap accountMap = (LinkedHashMap) value;

	        if ("email".equals(attributeKey)) {
	            return (String) accountMap.get("email");
	        } else if ("nickname".equals(attributeKey) || "profile_image_url".equals(attributeKey)) {
	            Object profile = accountMap.get("profile");
	            if (profile instanceof LinkedHashMap) {
	                LinkedHashMap profileMap = (LinkedHashMap) profile;
	                return (String) profileMap.get(attributeKey);
	            }
	        }
	    }
	    log.warn("Attribute '{}' not found in Kakao response", attributeKey);
	    return null;
	}




	
	
	
	private String getNaver(Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getFacebook(Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return null;
	}

	

	private String getGmail(Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return null;
	}
}











