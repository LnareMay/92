package com.lec.packages.config;

import javax.sql.DataSource;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.lec.packages.security.CustomUserDetailsService;
import com.lec.packages.security.handler.Custom403Handler;
import com.lec.packages.security.handler.CustomAuthenticationSuccessHandler;

import groovyjarjarantlr4.v4.parse.ANTLRParser.exceptionGroup_return;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
// 권한체크하는 어노테이션
// 게시글은 로그인한 사용자만 접근할 수 있어야 한다.(게시글목록은 로그인여부와 상관없이 조회가능)
// 권한을 설정하는 방법은 코드로 설정할 수도 있고 어노테이션을 이용해서 지정할 수 있다.
// 만약, 코드로할 경우에는 컨트롤러의 메서드를 작성한 후에 다시 설정해야 하는 번거로움이 있다.
// @EnableMethodSecurity의 prePostEnabled 속성은 권한을 체크하려는 메서드에 @PreAuthorize 또는
// @PostAuthorize어노테이션을 이용해서 사전 or 사후에 권한 체크를 할 수 있다. 
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class CustomSecurityConfig {

	private final DataSource dataSource;
	private final CustomUserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		log.info("---------------- filterChain -------------------");

		// 인증처리로직
		// http.formLogin(); 스프링시큐티에서 기본제공하는 로그인 화면
		// http.formLogin().loginPage("/member/login");
		http.formLogin(login -> login.loginPage("/member/login") // 로그인 페이지 URL
				// .defaultSuccessUrl("/") // 로그인 성공 후 이동할 URL
				.successHandler(new CustomAuthenticationSuccessHandler())
				.failureUrl("/member/login?error") // 로그인 실패 시
																											// 이동할 URL
				.usernameParameter("username") // 사용자명 파라미터 이름
				.passwordParameter("password")); // 비밀번호 파라미터 이름

		// 기본적으로 스프링시큐리티에서는 GET방식을 제외한 POST/PUT/DELETE 요청시에
		// CSRF 토큰을 요구하기 때문에 403(Forbidden)에러가 발생하기 때문에 CSRF요구를
		// 비활성화 시키면 username과 password만 로그인이 가능해 진다.
		// http.csrf().disable();
		http.csrf(csrf -> csrf.disable());

		// 자동로그인
		// 스프링시큐리티의 rememberMe기능은 쿠키를 이용해서 브라우저에 로그인했던 정보를
		// 유지하기 때문에 매번 로그인을 할 필요가 없어진다.
		// 쿠키값을 생성할 때 정보를 보관하기 위한 여러가지 방법이 있지만 가장 무난한 방법이
		// DB를 이용하는 것이다. 실습은 persistent-logins 테이블을 생성
		// create table persistent_logins (
		// username varchar(64) not null
		// , series varchar(64) primary key
		// , token varchar(64) not null
		// , last_used timestamp not null);
		// 생성된 테이블은 스프링시큐리티 내부에서 사용하기 때문에 변경하지 말아야 한다.
		// 로그인정보를 보관하려면 DataSource와 UserDetailsService타입의 객체가 필요하다.
		// 쿠키값을 생성할 때는 쿠키값을 인코딩하기 위하 key값과 필요한 정보를 보관할
		// tokenRepository를 지정하는데 코드상에서는 persistentTokenRepository()를 이용
		// http.rememberMe(me -> me.key("12345678")
		// .tokenRepository(persistentTokenRepository())
		// .userDetailsService(userDetailsService)
		// .tokenValiditySeconds(60*60*24*30)); // 유효기간 30일

		// 로그아웃 설정 추가
		http.logout(logout -> logout.logoutUrl("/member/logout") // 로그아웃 URL
				.logoutSuccessUrl("/") // 로그아웃 후 이동할 URL
				.invalidateHttpSession(true) // 세션 무효화
				.deleteCookies("JSESSIONID")); // 쿠키 삭제

		// 403에러 핸들링
		http.exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler()));

		http.authorizeRequests(auth -> auth
	            .requestMatchers("/member/login", "/member/join", "/css/**", "/js/**", "/images/**", "/").permitAll() // 로그인 페이지 및 정적 리소스 허용
	            .requestMatchers("/admin/**").hasRole("ADMIN") // ADMIN 권한이 필요한 경로
	            .anyRequest().authenticated() // 나머지 요청은 인증 필요
	        );
		
		return http.build();
		
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new Custom403Handler();
	}

	// static resource(css, js...)에 접근할 수 있도록 spring security에서 제외
//	@Bean
//	public WebSecurityCustomizer webSecurityCustomizer() {
//		log.info("--------------- WebSecurityCustomizer -------------------");
//		return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//	}

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
		repo.setDataSource(dataSource);
		return repo;
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
