package com.lec.packages.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@EntityListeners(AuditingEntityListener.class)
public class Facility extends BaseEntity{
    // Id => P.K 명시
    // @Column => 컬럼 정보 셋업
    // length => 컬럼 데이터 길이
    // name => 컬럼 이름
    // columnDefinition => 컬럼 데이터 타입 지정
    // @JoinColumn => 외래키 명시

	 	@Id
	    @Column(length = 20, name = "FACILITY_CODE")
	    private String facilityCode;

	    @Column(nullable = false, name = "FACILITY_NAME", length = 50)
	    private String facilityName;

	    @Column(nullable = false, name = "FACILITY_ADDRESS", length = 100)
	    private String facilityAddress;
	    
	    @Column(name = "FACILITY_ADDRESS_DETAIL", length = 100)
	    private String facilityAddressDetail;

	    @Column(nullable = false, name = "FACILITY_ZIPCODE", length = 20)
	    private String facilityZipcode;

	    @Column(columnDefinition = "TEXT", name = "FACILITY_DESCRIPTION")
	    private String facilityDescription;

	    @Column(name = "FACILITY_IMAGE_1",length = 255)
	    private String facilityImage1;

	    @Column(name = "FACILITY_IMAGE_2",length = 255)
	    private String facilityImage2;

	    @Column(name = "FACILITY_IMAGE_3", length = 255)
	    private String facilityImage3;

	    @Column(name = "FACILITY_IMAGE_4", length = 255)
	    private String facilityImage4;

	    @JoinColumn(name = "EXERCISE_CODE")
	    @Column(name = "EXERCISE_CODE", length = 20)
	    private String exerciseCode;
	    
	    @JoinColumn(name = "EXERCISE_NAME")
	    @Column(name = "EXERCISE_NAME", length = 50)
	    private String exerciseName;
  
	    @Column(name = "FACILITY_ISONLYCLUB")
	    private boolean facilityIsOnlyClub;

	    @JoinColumn(name = "MEM_ID")
	    @Column(name = "MEM_ID", length = 20)
	    private String memId;

	    // DECIMAL TYPE 선언
	    // precision 소수점 앞자리
	    // scale 소수점 뒷자리
	    @Column(name = "PRICE", columnDefinition = "DECIMAL", precision = 50, scale = 0)
	    private BigDecimal price;

	    @Column(name = "FACILITY_START_TIME")
	    private LocalDateTime facilityStartTime;

	    @Column(name = "FACILITY_END_TIME")
	    private LocalDateTime facilityEndTime;

	    @Column(name = "DELETE_FLAG")
	    private boolean deleteFlag;
	    
	    public void modifyFacility(String facilityName, String facilityDescription
	    							,boolean facilityIsOnlyClub, BigDecimal price
	    							,LocalDateTime facilityStartTime, LocalDateTime facilityEndTime) {
	    	
	    	this.facilityName = facilityName;
	    	this.facilityDescription = facilityDescription;
	    	this.facilityIsOnlyClub = facilityIsOnlyClub;
	    	this.price = price;
	    	this.facilityStartTime = facilityStartTime;
	    	this.facilityEndTime = facilityEndTime;
	    }
	    
	    
	    
}
