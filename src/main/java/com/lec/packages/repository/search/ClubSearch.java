package com.lec.packages.repository.search;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lec.packages.domain.Club;

public interface ClubSearch {

//	Page<Club> searchAllImpl(String[] types, String[] keywords, Pageable pageable);

	List<Club> getClubListWithMemID(String memId);
}
