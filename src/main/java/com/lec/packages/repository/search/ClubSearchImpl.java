package com.lec.packages.repository.search;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.lec.packages.domain.Club;
import com.lec.packages.domain.QClub;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ClubSearchImpl extends QuerydslRepositorySupport implements ClubSearch {

	
	public ClubSearchImpl() {
		super(Club.class);
	}

	/*
	@Override
	public Page<Club> searchAllImpl(String[] types, String[] keywords, Pageable pageable) {
	    QClub club = QClub.club;
	    JPQLQuery<Club> query = from(club);
	    BooleanBuilder builder = new BooleanBuilder();
	    
	    builder.and(club.deleteFlag.eq(false)); // deleteflag가 0인것만 조회
	    
	    // 주소 필터 추가
	    String region = "";
	    String originalRegion = "";
	    if (types != null && Arrays.asList(types).contains("address")) {
	        String addressKeyword = Arrays.stream(keywords)
	                                      .filter(keyword -> keyword != null && !keyword.isBlank())
	                                      .findFirst()
	                                      .orElse("");
	        if (!addressKeyword.isEmpty()) {
	        	// 원본 주소 : 서울특별시 서초구
	        	originalRegion = addressKeyword.trim();
	        	String[] originalParts = originalRegion.split(" ");
	            if (originalParts.length >= 2) {
	            	originalRegion = originalParts[0] + " " + originalParts[1];
	            } 
	        	
	        	addressKeyword = addressKeyword.replace("특별시", "") 
	        								   .replace("광역시", "")
	        								   .replace("도", "")
	        								   .replace("전라남도", "전남")
	        								   .replace("경상북도", "경북")
	        								   .trim(); 
	        	// 전처리 주소 : 서울 서초구
	            String[] addressParts = addressKeyword.split(" ");
	            if (addressParts.length >= 2) {
	            	region = addressParts[0] + " " + addressParts[1];
	            } 
	        }
	    }
	    
	    if (!region.isEmpty() || !originalRegion.isEmpty()) {
	    	BooleanBuilder addressBuilder = new BooleanBuilder();
	    	    
		    if (!region.isEmpty()) {
		    	addressBuilder.or(club.clubAddress.containsIgnoreCase(region));
		    }
		    if (!originalRegion.isEmpty()) {
		    	addressBuilder.or(club.clubAddress.containsIgnoreCase(originalRegion));
		    }
		    builder.and(addressBuilder);
	    }

	    // 테마 필터 추가
	    if (types != null && Arrays.asList(types).contains("theme")) {
	        String themeKeyword = keywords[1] != null ? keywords[1].trim().toLowerCase() : "";
	        log.info("Theme Keyword: {}", themeKeyword);
	        if (!themeKeyword.isEmpty() && !"all".equalsIgnoreCase(themeKeyword)) {
	            builder.and(club.clubTheme.containsIgnoreCase(themeKeyword));
	        }
	    }
	    
	    query.where(builder);
	    query.orderBy(club.CREATEDATE.asc());
	    getQuerydsl().applyPagination(pageable, query);

	    List<Club> clubs = query.fetch();
	    long total = query.fetchCount();
	    
//	    log.info("========Clubtheme Query: {}", query.toString());

	    return new PageImpl<>(clubs, pageable, total);
	}


}
