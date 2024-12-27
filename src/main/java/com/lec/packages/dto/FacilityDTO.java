package com.lec.packages.dto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDTO {
    
    @NotEmpty
    private String facilityCode;

    @NotEmpty
    private String facilityName;

    @NotEmpty
    private String facilityAddress;

    @NotEmpty
    private String facilityZipcode;
    
    private String facilityDescription;

    private String facilityImage1;
    
    private String facilityImage2;
    
    private String facilityImage3;
    
    private String facilityImage4;

    private String exerciseCode;

    private boolean facilityIsOnlyClub;

    @NotEmpty
    private String memId;

    @NotEmpty
    private BigDecimal price;

    @NotEmpty
    private LocalDateTime facilityStartTime;

    @NotEmpty
    private LocalDateTime facilityEndTime;
    
    @NotEmpty
    private LocalDateTime createDate;
    
    private LocalDateTime modifyDate;

    private boolean deleteFlag;
    
    //PRICE 포멧팅
    public String getFormattedPrice() {
        if (price == null) {
            return "0";
        }
        return NumberFormat.getNumberInstance(Locale.KOREA).format(price);
    }
    //createDate 포멧팅
    public String getFormattedCreateDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");
        return createDate != null ? createDate.format(formatter) : "";
    }
    
}
