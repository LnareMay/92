package com.lec.packages.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lec.packages.domain.Member;
import com.lec.packages.dto.ClubDTO;
import com.lec.packages.dto.MemberSecurityDTO;
import com.lec.packages.dto.ClubMemberDTO;
import com.lec.packages.dto.PageRequestDTO;
import com.lec.packages.dto.PageResponseDTO;
import com.lec.packages.repository.MemberRepository;
import com.lec.packages.security.CustomUserDetailsService;
import com.lec.packages.service.ClubService;
import com.lec.packages.service.KakaoApiService;
import com.lec.packages.service.MemberService;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ClubService clubService;
    private final MemberRepository memberRepository;
    private final KakaoApiService kakaoApiService;

    @GetMapping("/")
    public String mainClub(PageRequestDTO pageRequestDTO, HttpServletRequest request, Model model,
                           @RequestParam(value = "theme", required = false) String clubTheme) {

        // 현재 사용자 가져오기
        Member member = getAuthenticatedMember();
        if (member != null) {
            model.addAttribute("member", member); // Member 객체를 모델에 추가
        }

        // 지역 정보 추가
        try {
            String region = kakaoApiService.extractRegionFromAuthenticatedMember();
            model.addAttribute("region", region); // 구/동/면 값을 모델에 추가
        } catch (Exception e) {
            model.addAttribute("region", "지역 정보를 가져올 수 없습니다."); // 예외 처리
            e.printStackTrace();
        }

        String requestURI = request.getRequestURI();
        model.addAttribute("currentURI", requestURI); // requestURI를 모델에 추가

        if (clubTheme == null || clubTheme.isEmpty()) {
            clubTheme = "ALL";
        }

        PageResponseDTO<ClubDTO> responseDTO = "ALL".equals(clubTheme) ? clubService.list(pageRequestDTO)
                : clubService.listByTheme(pageRequestDTO, clubTheme);

        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("theme", clubTheme);

        return "index";
    }

    private Member getAuthenticatedMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof MemberSecurityDTO) {
            MemberSecurityDTO memberDTO = (MemberSecurityDTO) authentication.getPrincipal();
            Optional<Member> memberOptional = memberRepository.findById(memberDTO.getMemId());
            return memberOptional.orElse(null); // DB에서 Member 객체 조회
        }
        return null; // 인증되지 않은 사용자일 경우 null 반환
    }
}
