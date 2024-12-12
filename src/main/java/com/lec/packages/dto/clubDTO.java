package com.lec.packages.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class clubDTO {
    // @NotEmpty => Not null

    @NotEmpty
    private String CLUB_CODE;

    @NotEmpty
    private String CLUB_NAME;

    private String CLUB_EXERCISE;

    private String CLUB_INTRODUCTION;

    private String CLUB_THEME;

    @NotEmpty
    private long CLUB_IMAGE_1;
    @NotEmpty
    private long CLUB_IMAGE_2;
    @NotEmpty
    private long CLUB_IMAGE_3;
    @NotEmpty
    private long CLUB_IMAGE_4;

    private String CLUB_ADDRESS;

    private String MEM_ID;

    private boolean CLUB_ISPRIVATE;

    private String CLUB_PW;
    
    private boolean DELETE_FLAG;

    private LocalDateTime CREATEDATE;
    private LocalDateTime MODIFYDATE;
}
