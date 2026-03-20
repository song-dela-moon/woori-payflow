package com.card.payment.clearingsettlement.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponse {
    private boolean success;
    private String transferId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private LocalDateTime transferDate;
    private String responseCode;
    private String responseMessage;
    private String failureReason;
}