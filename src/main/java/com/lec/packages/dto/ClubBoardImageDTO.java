package com.lec.packages.dto;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClubBoardImageDTO {
    
    private String uuid;

    private String boardImage;

    private int ord;
}
