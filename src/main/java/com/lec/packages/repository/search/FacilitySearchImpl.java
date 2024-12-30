package com.lec.packages.repository.search;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;


import com.lec.packages.domain.Facility;
import com.lec.packages.domain.QFacility;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class FacilitySearchImpl extends QuerydslRepositorySupport implements FacilitySearch{
	
	public FacilitySearchImpl() {
		super(Facility.class);
	}
	
	@Override
	public Page<Facility> searchByUser(String userId, Pageable pageable){
		
		QFacility facility = QFacility.facility;
		JPQLQuery<Facility> query = from(facility);
		
		// 로그인된 사용자 ID로 필터링
		query.where(facility.memId.eq(userId));
		// 페이징 적용
		this.getQuerydsl().applyPagination(pageable, query);
		
		List<Facility> list = query.fetch();
		long count = query.fetchCount();
		
		return new PageImpl<>(list, pageable, count);
	}
	
	//검색 및 리스트 
	@Override
	public Page<Facility> searchAllImpl(String[] types, String[] keywords, Pageable pageable) {
		
		QFacility facility = QFacility.facility;
		JPQLQuery<Facility> query = from(facility);
		
		
		if (types != null && types.length > 0 && keywords != null && keywords.length > 0) {
	        BooleanBuilder booleanBuilder = new BooleanBuilder();

	        for (String type : types) {
	            switch (type) {
	                case "a": // 지역 검색
	                    for (String keyword : keywords) {
	                        booleanBuilder.or(facility.facilityAddress.contains(keyword));
	                    }
	                    break;

	                case "e": // 운동 유형 검색
	                    for (String keyword : keywords) {
	                        booleanBuilder.or(facility.exerciseCode.contains(keyword));
	                    }
	                    break;

	                case "ae": // 지역 + 운동 유형 검색
	                    if (keywords.length >= 2) { // 최소 두 개의 키워드가 있어야 함
	                        booleanBuilder.or(
	                            facility.facilityAddress.contains(keywords[0])
	                                .and(facility.exerciseCode.contains(keywords[1]))
	                        );
	                    }
	                    break;

	                default:
	                    throw new IllegalArgumentException("Invalid search type: " + type);
	            }
	        }

	        
	        query.where(booleanBuilder);
	    }
		

		this.getQuerydsl().applyPagination(pageable, query);
		
	    List<Facility> list = query.fetch();
	    long count = query.fetchCount();
	    
	    return new PageImpl<>(list, pageable, count);
	}
}
