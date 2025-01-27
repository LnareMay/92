package com.lec.packages.dto;

import java.math.BigDecimal;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDTO {
    
	
    private String facilityCode;

    @NotNull
    private String facilityName;

    @NotNull
    private String facilityAddress;

    private String facilityAddressDetail;
    
    @NotNull
    private String facilityZipcode;
    
    private String facilityDescription;

    @NotNull
    private String facilityImage1;
    
    private String  facilityImage2;
    
    private String  facilityImage3;
    
    private String  facilityImage4;

    private String exerciseCode;
    
    private String exerciseName;

    private boolean facilityIsOnlyClub;

    @NotNull
    private String memId;

    @NotNull
    private BigDecimal price;

    @NotNull(message = "시설 시작 시간은 필수 입력 항목입니다.")
    private LocalTime facilityStartTime;

    @NotNull(message = "시설 종료 시간은 필수 입력 항목입니다.")
    private LocalTime facilityEndTime;
    
    
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
