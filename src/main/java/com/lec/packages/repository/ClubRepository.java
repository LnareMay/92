package com.lec.packages.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lec.packages.domain.Club;
import com.lec.packages.repository.search.ClubSearch;

public interface ClubRepository extends JpaRepository<Club, String>, ClubSearch {
	
    // deleteFlag가 1이 아닌 클럽만 가져오는 기본 메서드
    List<Club> findByDeleteFlagFalse();
    
    // 페이지네이션을 적용한 목록 조회
    @Query("SELECT c FROM Club c WHERE c.deleteFlag = false order by CREATEDATE")
    Page<Club> findAllActiveClubs(Pageable pageable);
     
    // 테마별로 deleteFlag가 1이 아닌 클럽만 조회
    @Query("SELECT c FROM Club c WHERE c.deleteFlag = false AND c.clubTheme LIKE %:clubTheme% order by CREATEDATE")
    Page<Club> findByClubThemeContaining(@Param("clubTheme") String clubTheme, Pageable pageable);
    
    /* 주소기반, 테마별 deleteFlag가 1이 아닌 클럽만 조회
    @Query("SELECT c FROM Club c WHERE c.deleteFlag = false AND c.clubAddress LIKE %:memAddressSet% AND "
    		+ "c.clubTheme LIKE %:clubTheme% order by c.CREATEDATE")
    Page<Club> findByClubAddressAndTheme(@Param("memAddressSet") String memberAddress, @Param("clubTheme") String clubTheme, Pageable pageable);
     */
    // 클럽 방장인지 체크
    Long countByMemId(String memId);

    List<Club> findByMemId(String username);

    @Query(value = "select c.* from club c inner join club_member_list cml on c.CLUB_CODE = cml.CLUB_CODE where cml.mem_id =:memId and cml.DELETE_FLAG is false and c.DELETE_FLAG is false", nativeQuery = true)
    List<Club> getClubListWithMemID(@Param("memId") String memId);
}
