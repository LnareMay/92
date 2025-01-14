package com.lec.packages.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
	
	@NotNull
	private String reservationCode;
	
    private String facilityCode;

    private String facilityName;

    private String memId;


    @NotNull(message = "시설 예약 시간은 필수 입력 항목입니다.")
    private LocalTime reservationStartTime;


    @NotNull(message = "시설 예약 시간은 필수 입력 항목입니다.")
    private LocalTime reservationEndTime;

    @NotNull(message = "시설 예약일은 필수 입력 항목입니다.")
    private Date reservationDate;
    
    @NotNull
    private int count;
    
    @NotNull
    private BigDecimal price;
    
    @NotNull
    private String reservationProgress;
    
    @NotNull
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
