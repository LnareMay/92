package com.lec.admin.dto;

import java.math.BigDecimal;
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
public class facilityDTO {
    
    @NotEmpty
    private String FACILITY_CODE;

    @NotEmpty
    private String FACILITY_NAME;

    @NotEmpty
    private String FACILITY_ADDRESS;

    @NotEmpty
    private String FACILITY_ZIPCODE;
    
    private String FACILITY_DESCRIPTION;

    private long FACILITY_IMAGE_1;
    
    private long FACILITY_IMAGE_2;
    
    private long FACILITY_IMAGE_3;
    
    private long FACILITY_IMAGE_4;

    private String EXERCISE_CODE;

    private boolean FACILITY_ISONLYCLUB;

    @NotEmpty
    private String MEM_ID;

    private BigDecimal PRICE;

    private LocalDateTime FACILITY_START_TIME;

    private LocalDateTime FACILITY_END_TIME;

    private boolean DELETE_FLAG;
}
