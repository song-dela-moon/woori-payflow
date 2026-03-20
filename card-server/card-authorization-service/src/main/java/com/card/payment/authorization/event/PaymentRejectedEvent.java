package com.card.payment.authorization.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRejectedEvent {
    private String eventId;
    private String eventType;
    private String transactionId;
    private String merchantId;
    private String cardNumberMasked;
    private BigDecimal amount;
    private String responseCode;
    private String message;
    private LocalDateTime rejectedAt;
}