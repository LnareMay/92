package com.lec.packages.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.lec.packages.domain.Member;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferHistoryDTO {
    private String transferCode; 
    private String payCode;
    private Member senderId;          // ID of the member who sends money
    private Member receiverId;        // ID of the member who receives money
    private BigDecimal amount;        // Amount transferred
    private LocalDateTime transferDate; // Date and time of the transfer
    private String status;            // Status of the transfer (e.g., Success, Failed)
    private String memo;              // Memo for the transfe
    private String clubCode;
}
