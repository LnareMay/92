package com.lec.packages.dto;

import groovy.transform.builder.Builder;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubBoardDTO {
    
    @NotEmpty
    private String CLUB_CODE;

    private Long BOARD_NO;
    
    private String BOARD_TYPE;

    private String BOARD_TEXT;

    private String MEM_ID;

    private Boolean DELETE_FLAG;
}
