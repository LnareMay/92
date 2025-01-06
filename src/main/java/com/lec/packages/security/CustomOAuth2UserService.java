package com.lec.packages.security;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lec.packages.domain.Member;
import com.lec.packages.domain.MemberRole;
import com.lec.packages.dto.MemberSecurityDTO;
import com.lec.packages.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("======> User Request: {}", userRequest);

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();
        log.info("Client Name: {}", clientName);

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> paramMap = oAuth2User.getAttributes();

        String email = null;
        String nickname = null;
        String picture = null;
        String phone = null;
        boolean gender = true;
        String birthday = null;
        String name = null;

        if ("kakao".equalsIgnoreCase(clientName)) {
            email = extractKakaoAttribute(paramMap, "email");
            nickname = extractKakaoAttribute(paramMap, "nickname");
            picture = extractKakaoAttribute(paramMap, "profile_image_url");
        } else if ("naver".equalsIgnoreCase(clientName)) {
            Map<String, Object> response = (Map<String, Object>) paramMap.get("response");
            email = (String) response.get("email");
            nickname = (String) response.get("nickname");
            picture = (String) response.get("profile_image");
            phone = processPhoneNumber((String) response.get("mobile"));
            gender = "M".equalsIgnoreCase((String) response.get("gender"));
            birthday = (String) response.get("birthday");
            name = (String) response.get("name");
        }

        log.info("Extracted Email: {}", email);
        log.info("Extracted Nickname: {}", nickname);
        log.info("Extracted Picture: {}", picture);
        log.info("Extracted Phone: {}", phone);
        log.info("Extracted Gender: {}", gender);
        log.info("Extracted Birthday: {}", birthday);
        log.info("Extracted Name: {}", name);

        MemberSecurityDTO memberSecurityDTO = generateDTO(email, nickname, picture, phone, gender, birthday, name, true, false, false);
        log.info("Generated MemberSecurityDTO: {}", memberSecurityDTO);

        return memberSecurityDTO;
    }

    /**
     * 전화번호에서 "-"를 제거
     */
    private String processPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return null;
        }
        return phone.replaceAll("-", "");
    }

    @Transactional
    private MemberSecurityDTO generateDTO(String email, String nickname, String picture, String phone, boolean gender, String birthday, String name, boolean social, boolean manager, boolean delete) {
        Optional<Member> result = memberRepository.findByMemEmail(email);

        if (result.isEmpty()) {
            // 신규 사용자 생성
            Member newMember = createNewMember(email, nickname, picture, phone, gender, birthday, name, social, manager, delete);
            memberRepository.save(newMember);
            return createMemberSecurityDTO(newMember);
        } else {
            // 기존 사용자 처리
            Member existingMember = result.get();
            return createMemberSecurityDTO(existingMember);
        }
    }

    /**
     * 신규 사용자 생성
     */
    private Member createNewMember(String email, String nickname, String picture, String phone, boolean gender, String birthday, String name, boolean social, boolean manager, boolean delete) {
        Member member = Member.builder()
                .memId(email) // 소셜 로그인에서는 email을 ID로 사용
                .memPw(passwordEncoder.encode(UUID.randomUUID().toString())) // 랜덤 비밀번호 생성
                .memEmail(email)
                .memNickname(nickname)
                .memPicture(picture)
                .memTell(phone)
                .memName(name)
                .memGender(gender)
                .memBirthday(birthday)
                .memSocial(social) // 소셜 로그인 여부
                .memIsmanager(manager) // 기본값: 관리자 아님
                .deleteFlag(delete) // 기본값: 삭제되지 않음
                .build();

        // 기본 역할 추가
        if (member.getRoleSet() == null) {
            member.setRoleSet(new HashSet<>());
        }
        member.addRole(MemberRole.USER);

        return member;
    }

    /**
     * MemberSecurityDTO 생성
     */
    private MemberSecurityDTO createMemberSecurityDTO(Member member) {
        log.info("Creating DTO: memSocial={}, deleteFlag={}", member.isMemSocial(), member.isDeleteFlag());
        return new MemberSecurityDTO(
                member.getMemId(),
                member.getMemPw(),
                member.getMemName(),
                member.getMemNickname(),
                member.getMemExercise(),
                member.getMemClub(),
                member.getMemPicture(),
                member.getMemIntroduction(),
                member.isMemGender(),
                member.getMemTell(),
                member.getMemEmail(),
                member.getMemBirthday(),
                member.getMemAddress(),
                member.getMemAddressDetail(),
                member.getMemZipcode(),
                member.getMemAddressSet(),
                member.isMemIsmanager(),
                member.isMemSocial(),
                member.isDeleteFlag(),
                member.getRoleSet()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
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
}
