package com.lec.packages.repository.search;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;


import com.lec.packages.domain.Facility;
import com.lec.packages.domain.QFacility;
import com.lec.packages.dto.PageRequestDTO;
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


	/*검색 및 리스트 
	@Override
	public Page<Facility> searchAllImpl(String[] types, String[] keywords, Pageable pageable) {
	    QFacility facility = QFacility.facility;
	    JPQLQuery<Facility> query = from(facility);

	    // BooleanBuilder 초기화
	    BooleanBuilder booleanBuilder = new BooleanBuilder();
	    
	    booleanBuilder.and(facility.deleteFlag.eq(false)); 

	    if (types != null && types.length > 0 && keywords != null && keywords.length > 0) {
	        for (String type : types) {
	            switch (type) {
	                case "a": // 지역 검색
	                    if (keywords[0] != null && !keywords[0].isEmpty()) {
	                        booleanBuilder.and(facility.facilityAddress.contains(keywords[0]));
	                    }
	                    break;

	                case "e": // 운동 유형 검색
	                    if (keywords[1] != null && !keywords[1].isEmpty()) {
	                        booleanBuilder.and(facility.exerciseCode.contains(keywords[1]));
	                    }
	                    break;

	                case "c": // 예약 유형 검색
	                	if (keywords[2] != null && !keywords[2].isEmpty()) {
	                		boolean isOnlyClub = Boolean.parseBoolean(keywords[2]);
	                		booleanBuilder.and(facility.facilityIsOnlyClub.eq(isOnlyClub));
	                	} else {
	                		booleanBuilder.and(facility.facilityIsOnlyClub.eq(true).or(facility.facilityIsOnlyClub.eq(false)));
	                	}
	                	
	                case "ae": // 지역 + 운동 유형 검색
	                    if (keywords[0] != null && !keywords[0].isEmpty()) {
	                        booleanBuilder.and(facility.facilityAddress.contains(keywords[0]));
	                    }
	                    if (keywords[1] != null && !keywords[1].isEmpty()) {
	                        booleanBuilder.and(facility.exerciseCode.contains(keywords[1]));
	                    }	                    
	              
	                   
	                case "aec": // 지역 + 운동 + 예약 유형 검색
	                	if (keywords[0] != null && !keywords[0].isEmpty()) {
	                		booleanBuilder.and(facility.facilityAddress.contains(keywords[0]));
	                	}
	                	if (keywords[1] != null && !keywords[1].isEmpty()) {
	                		booleanBuilder.and(facility.exerciseCode.contains(keywords[1]));
	                	}
	                	if(keywords[2] != null && !keywords[2].isEmpty()) {
	                		boolean isOnlyClub = Boolean.parseBoolean(keywords[2]);
	                		booleanBuilder.and(facility.facilityIsOnlyClub.eq(isOnlyClub));
	                	}
	                    break;

	                default:
	                    throw new IllegalArgumentException("Invalid search type: " + type);
	            }
	        }
	        query.where(booleanBuilder);
	    }

	    // 페이징 적용
	    getQuerydsl().applyPagination(pageable, query);

	    // 결과 조회 및 반환
	    List<Facility> list = query.fetch();
	    long count = query.fetchCount();

	    return new PageImpl<>(list, pageable, count);
	}
	*/
	
}
