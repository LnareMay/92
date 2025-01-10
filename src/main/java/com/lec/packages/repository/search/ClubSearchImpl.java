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

	@Override
	public Page<Club> searchAllImpl(String[] types, String[] keywords, Pageable pageable) {
	    QClub club = QClub.club;
	    JPQLQuery<Club> query = from(club);
	    BooleanBuilder builder = new BooleanBuilder();
	    
	    builder.and(club.deleteFlag.eq(false)); // deleteflag가 0인것만 조회

	    // 주소 필터 추가
	    if (types != null && Arrays.asList(types).contains("address")) {
	        String addressKeyword = Arrays.stream(keywords)
	                                      .filter(keyword -> keyword != null && !keyword.isBlank())
	                                      .findFirst()
	                                      .orElse("");
	        if (!addressKeyword.isEmpty()) {
	            builder.and(club.clubAddress.containsIgnoreCase(addressKeyword));
	        }
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
	    
	    log.info("========Clubtheme Query: {}", query.toString());

	    return new PageImpl<>(clubs, pageable, total);
	}
}
