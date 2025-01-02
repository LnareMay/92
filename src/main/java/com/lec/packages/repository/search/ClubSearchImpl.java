package com.lec.packages.repository.search;

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
		
		
		this.getQuerydsl().applyPagination(pageable, query);
		List<Club> list = query.fetch();
		long count = query.fetchCount();
		
		return new PageImpl<>(list, pageable, count);
	}

}
