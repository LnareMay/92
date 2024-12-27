package com.lec.packages.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lec.packages.domain.Club;
import com.lec.packages.repository.search.ClubSearch;

public interface ClubRepository extends JpaRepository<Club, String>, ClubSearch {

	@Query("SELECT c FROM Club c WHERE c.clubTheme LIKE %:clubTheme%")
	List<Club> findByClubThemeContaining(@Param("clubTheme") String clubTheme);

//	@Query("SELECT c FROM club_member_list m ")
//	Optional<Club> findById(clubDTO.getClubCode());
	
	//List<Club> findAllActiveClub();
	
	// @Query("SELECT c FROM Club c WHERE c.deleteFlag = false")
	// Optional<Club> findByClubCodeAndDeleteFlagFalse(String clubCode); // 삭제되지않는 클럽만 조회

}
