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
public class ClubDTO {
    // @NotEmpty => Not null

    @NotEmpty
    private String clubCode;

    @NotEmpty
    private String clubName;

    private String clubExercise;

    private String clubIntroduction;

    private String clubTheme;

//    @NotEmpty
    private String clubImage1;

    private String clubImage2;

    private String clubImage3;

    private String clubImage4;

    private String clubAddress;

    @NotEmpty
    private String memId;

    private boolean clubIsprivate;

    private String clubPw;
    
    private boolean deleteFlag;

    private LocalDateTime CREATEDATE;
    private LocalDateTime MODIFYDATE;

}
