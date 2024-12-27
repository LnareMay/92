package com.lec.packages.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@ToString
public class Club_Member_List extends BaseEntity {

    @JoinColumn(name = "CLUB_CODE")
	private String clubCode;
	
    @JoinColumn(name = "MEM_ID")
	private String memId;
    
    @Column(name = "BOARD_COUNT")
    private int boardCount;
    
    @Column(name = "DELETE_FLAG")
    private boolean deleteFlag;
    
    
	
}
