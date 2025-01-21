package com.lec.packages.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lec.packages.domain.Reservation_Member_List;
import com.lec.packages.domain.primaryKeyClasses.ClubReservationMemberKeyClass;

public interface ClubReservationMemberRepository extends JpaRepository<Reservation_Member_List, ClubReservationMemberKeyClass>{

    
}