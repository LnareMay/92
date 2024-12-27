package com.lec.packages.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lec.packages.dto.ClubBoardAllListDTO;

public interface ClubBoardSearch {

    Page<ClubBoardAllListDTO> searchWithAll(String[] types, Pageable pageable);
}
