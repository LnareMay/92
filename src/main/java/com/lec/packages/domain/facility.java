package com.lec.packages.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class facility extends BaseEntity{
    // Id => P.K 명시
    // @Column => 컬럼 정보 셋업
    // length => 컬럼 데이터 길이
    // name => 컬럼 이름
    // columnDefinition => 컬럼 데이터 타입 지정
    // @JoinColumn => 외래키 명시

    @Id
    @Column(length = 20, name = "FACILITY_CODE")
    private String FACILITY_CODE;

    @Column(nullable = false, name = "FACILITY_NAME", length = 50)
    private String FACILITY_NAME;

    @Column(nullable = false, name = "FACILITY_ADDRESS", length = 100)
    private String FACILITY_ADDRESS;

    @Column(nullable = false, name = "FACILITY_ZIPCODE", length = 20)
    private String FACILITY_ZIPCODE;

    @Column(name = "FACILITY_IMAGE_1", columnDefinition = "LONGBLOB")
    private long FACILITY_IMAGE_1;
    @Column(name = "FACILITY_IMAGE_2", columnDefinition = "LONGBLOB")
    private long FACILITY_IMAGE_2;
    @Column(name = "FACILITY_IMAGE_3", columnDefinition = "LONGBLOB")
    private long FACILITY_IMAGE_3;
    @Column(name = "FACILITY_IMAGE_4", columnDefinition = "LONGBLOB")
    private long FACILITY_IMAGE_4;

    @JoinColumn(name = "EXERCISE_CODE")
    @Column(name = "EXERCISE_CODE", length = 20)
    private String EXERCISE_CODE;

    @Column(name = "FACILITY_ISONLYCLUB")
    private boolean FACILITY_ISONLYCLUB;

    @JoinColumn(name = "MEM_ID")
    @Column(name = "MEM_ID", length = 20)
    private String MEM_ID;

    // DECIMAL TYPE 선언
    // precision 소수점 앞자리
    // scale 소수점 뒷자리
    @Column(name = "PRICE", columnDefinition = "DECIMAL", precision = 50, scale = 0)
    private BigDecimal PRICE;

    @Column(name = "FACILITY_START_TIME")
    private LocalDateTime FACILITY_START_TIME;
    @Column(name = "FACILITY_END_TIME")
    private LocalDateTime FACILITY_END_TIME;

    @Column(name = "DELETE_FLAG")
    private boolean DELETE_FLAG;
}
