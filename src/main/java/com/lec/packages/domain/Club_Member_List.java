package com.lec.packages.domain;

import com.lec.packages.domain.primaryKeyClasses.ClubMemberKeyClass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(ClubMemberKeyClass.class)
@ToString
public class Club_Member_List extends BaseEntity {

	@Id
    @JoinColumn(name = "MEM_ID")
    @Column(nullable = false, length = 100, name = "MEM_ID")
    private String memId;
    
	@Id
    @JoinColumn(name = "CLUB_CODE")
    @Column(length = 10, name = "CLUB_CODE")
    private String clubCode;
    
    @Column(name = "BOARD_COUNT")
    private int boardCount;
    
    @Column(name = "DELETE_FLAG")
    private boolean deleteFlag;    
    
}
