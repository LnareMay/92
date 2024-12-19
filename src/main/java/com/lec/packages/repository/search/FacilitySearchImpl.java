package com.lec.packages.repository.search;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.lec.packages.domain.Qfacility;
import com.lec.packages.domain.facility;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class FacilitySearchImpl extends QuerydslRepositorySupport implements FacilitySearch{
	
	public FacilitySearchImpl() {
		super(facility.class);
	}

	@Override
	public Page<facility> searchAll(String[] types, String keyword, Pageable pageable) {
		
		Qfacility facility = Qfacility.facility;
		JPQLQuery<facility> query = from(facility);
		
		if((types!= null || types.length >0) && keyword != null) {
			BooleanBuilder booleanBuilder = new BooleanBuilder();
			for(String type:types) {
				switch(type) {
				case "c": 
					booleanBuilder.or(facility.facilityCode.contains(keyword));
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
			List<facility> list = query.fetch();
			long count = query.fetchCount();
			log.info("검색건수 = " + count);
			
			return null;
	}
	
	@Override
	public Page<facility> searchAllImpl(String[] types, String keyword, Pageable pageable) {
		
		Qfacility facility = Qfacility.facility;
		JPQLQuery<facility> query = from(facility);
		

		if((types!= null || types.length >0) && keyword != null) {
			BooleanBuilder booleanBuilder = new BooleanBuilder();
			for(String type:types) {
				switch(type) {
				case "c": 
					booleanBuilder.or(facility.facilityCode.contains(keyword));
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
		List<facility> list = query.fetch();
		long count=query.fetchCount();
		
		return new PageImpl<>(list, pageable, count);
	}

}
