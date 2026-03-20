package com.card.payment.clearingsettlement.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentApprovedEvent {

    private String eventId;
    private String eventType;
    private String transactionId;
    private String approvalNumber;
    private String merchantId;
    private String cardNumberMasked;
    private BigDecimal amount;
    private String responseCode;
    private LocalDateTime approvedAt;
}