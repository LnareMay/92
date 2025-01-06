package com.lec.packages.security.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.lec.packages.dto.MemberSecurityDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/*
  스프링시큐리티는 로그인 성공과 실패를 커스터마이징할 수 있도록 AuthenticationSuccessHandler와
  AuthenticationFailedHandler인터페이스를 제공한다.
  
  실습에서 소셜로그인성공한 후에 특정페이지로 이동하는 방법을 처리해야 하는데 AuthenticationSuccessHandler를
  이용해서 처리하도록 한다.
*/

@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler{

	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
	    log.info("------------------------------------------");
	    log.info("CustomSocialLoginSuccessHandler.onAuthenticationSuccess() 호출............");
	    log.info(authentication.getPrincipal());

	    // 로그인 사용자 정보 가져오기
	    MemberSecurityDTO memberSecurityDTO = (MemberSecurityDTO) authentication.getPrincipal();
	    log.info("Authenticated User: {}", memberSecurityDTO);

	    // 소셜 로그인 이메일 가져오기
	    String id = memberSecurityDTO.getMemId();
	    log.info("소셜 로그인 이메일: {}", id);

	    // 이메일 기반 리다이렉트 URL 생성
	    String redirectUrl = "/";

	    log.info("리다이렉트 URL: {}", redirectUrl);

	    // 이메일 기반 경로로 리다이렉트
	    response.sendRedirect(redirectUrl);
	    
	    
	}

	
}
