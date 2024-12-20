package com.lec.packages.security.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // 사용자 권한 가져오기
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // ADMIN 권한이 있는 경우
        for (GrantedAuthority authority : authorities) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                response.sendRedirect("/admin/main"); // ADMIN 사용자 리다이렉트
                return;
            }
        }

        // 일반 사용자 기본 리다이렉트
        response.sendRedirect("/");
    }
}

