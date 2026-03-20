package com.pg.webhook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentWebhookPayload {

    private String eventType;     // PAYMENT_APPROVED, PAYMENT_FAILED, PAYMENT_CANCELLED
    private String paymentId;
    private String orderId;
    private Long amount;
    private String status;        // APPROVED, FAILED, CANCELLED
    private String approvalCode;
    private String failureCode;
    private String failureMessage;
}
