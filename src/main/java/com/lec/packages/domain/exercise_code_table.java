package com.lec.packages.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class exercise_code_table {
    
    @Id
    @Column(name = "EXERCISE_CODE", length = 20)
    private String EXERCISE_CODE;

    @Column(nullable = false, length = 50)
    private String EXERCISE_NAME;
}
