package com.lec.packages.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="exercise_code_table")
public class ExerciseCodeTable {
    // Id => P.K 명시
    // @Column => 컬럼 정보 셋업
    // length => 컬럼 데이터 길이
    // name => 컬럼 이름
    // columnDefinition => 컬럼 데이터 타입 지정
    // @JoinColumn => 외래키 명시

	@Id
    @Column(name = "EXERCISE_CODE", length = 20)
    private String exerciseCode;

    @Column(name = "EXERCISE_NAME", length = 50, nullable = false)
    private String exerciseName;
}
