package com.lec.packages.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lec.packages.domain.Club;
import com.lec.packages.repository.search.ClubSearch;

public interface ClubRepository extends JpaRepository<Club, String>, ClubSearch {

	//List<Club> findAllActiveClub();
	
	@Query("SELECT c FROM Club c WHERE c.deleteFlag = false")
	Optional<Club> findByClubCodeAndDeleteFlagFalse(String clubCode); // 삭제되지않는 클럽만 조회

}
