package com.lec.packages.repository.search;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;


import com.lec.packages.domain.QReservation;
import com.lec.packages.domain.Reservation;
import com.querydsl.jpa.JPQLQuery;

public class ReservationSearchImpl extends QuerydslRepositorySupport implements ReservationSearch{
	
	public ReservationSearchImpl() {
		super(Reservation.class);
	}
	
	
	@Override
	public Page<Reservation> searchByUser(String memId, Pageable pageable){
		
		QReservation reservation = QReservation.reservation;
		JPQLQuery<Reservation> query = from(reservation);
		
		// 로그인된 사용자 ID로 필터링
		query.where(reservation.memId.eq(memId));
		// 페이징 적용
		this.getQuerydsl().applyPagination(pageable, query);
		
		List<Reservation> list = query.fetch();
		long count = query.fetchCount();
		
		return new PageImpl<>(list, pageable, count);
	}
	

}
