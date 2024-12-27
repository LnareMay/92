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

	@Override
	public Page<Facility> searchAll(String[] types, String keyword, Pageable pageable) {
		
		QFacility facility = QFacility.facility;
		JPQLQuery<Facility> query = from(facility);
		
		if((types!= null || types.length >0) && keyword != null) {
			BooleanBuilder booleanBuilder = new BooleanBuilder();
			for(String type:types) {
				switch(type) {
				case "m": 
					booleanBuilder.or(facility.memId.contains(keyword));
					break;
				case "n": 
					booleanBuilder.or(facility.facilityName.contains(keyword));
					break;
				case "a": 
					booleanBuilder.or(facility.facilityAddress.contains(keyword));
				}
		}	
			query.where(booleanBuilder);
		}
		
			this.getQuerydsl().applyPagination(pageable, query);
			List<Facility> list = query.fetch();
			long count = query.fetchCount();
			log.info("검색건수 = " + count);
			
			return null;
	}
	
	@Override
	public Page<Facility> searchAllImpl(String[] types, String keyword, Pageable pageable) {
		
		QFacility facility = QFacility.facility;
		JPQLQuery<Facility> query = from(facility);
		

		if((types!= null || types.length >0) && keyword != null) {
			BooleanBuilder booleanBuilder = new BooleanBuilder();
			for(String type:types) {
				switch(type) {
				case "m": 
					booleanBuilder.or(facility.memId.contains(keyword));
					break;
				case "n": 
					booleanBuilder.or(facility.facilityName.contains(keyword));
					break;
				case "a": 
					booleanBuilder.or(facility.facilityAddress.contains(keyword));
				}
		}	
			query.where(booleanBuilder);
		}
		
		this.getQuerydsl().applyPagination(pageable, query);
		List<Facility> list = query.fetch();
		long count=query.fetchCount();
		
		return new PageImpl<>(list, pageable, count);
	}

}
