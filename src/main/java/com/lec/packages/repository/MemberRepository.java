package com.lec.packages.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lec.packages.domain.Member;

public interface MemberRepository extends JpaRepository<Member, String>{
	// 카카오등 social서비스를 통해서 회원가입된 회원들은 제외하고 일반회원들만
	// 가져오도록 social속성이 false인 사용자만 대상으로 한다.
	@EntityGraph(attributePaths = {"roleSet", "memExercise", "memClub"})
	@Query("SELECT m FROM Member m " +
		       "LEFT JOIN FETCH m.memExercise e " +
		       "LEFT JOIN FETCH m.memClub c " +
		       "WHERE m.memId = :memId")
	Optional<Member> getWithRoles(@Param("memId") String mid);

	/*
	  소셜로그인에 사용한 이메일이 존재하는 경우와 그렇제 않은 경우에 처리방법이 중요하다.
	  social의 email과 같은 이메일을 가진 회원이 있을 경우 로그인 자체가 완료되어야 한다.
	  Member.social속성을 이용해서 만약 악의적인 사용자가 현재 사용자의 이메일을 안다고 해도
	  직접 로그인할 때는 social설정이 false인 경우만 조회되기 때문에 로그인이 되지 않는다.
	  대신에 소셜서비스를 통해서 로그인한 사용자의 경우 일반 로그인을 하기 위해서는 일반회원
	  으로 전환할 수 있는 기능(화면)이 제공되어야 한다.
	*/
	@EntityGraph(attributePaths = {"roleSet", "memExercise", "memClub"})
	@Query("SELECT m FROM Member m " +
		       "LEFT JOIN FETCH m.memExercise e " +
		       "LEFT JOIN FETCH m.memClub c " +
		       "WHERE m.memEmail = :memEmail and m.memSocial = true")
	Optional<Member> findByMemEmail(@Param("memEmail") String memEmail);
}
