package com.lec.packages.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lec.packages.domain.member;

public interface MemberRepository extends JpaRepository<member, String>{
    
}
