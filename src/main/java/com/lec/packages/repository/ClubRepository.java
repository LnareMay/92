package com.lec.packages.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lec.packages.domain.Club;
import com.lec.packages.repository.search.ClubSearch;

public interface ClubRepository extends JpaRepository<Club, String>, ClubSearch {


}
