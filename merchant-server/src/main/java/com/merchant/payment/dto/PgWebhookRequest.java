package com.merchant.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PgWebhookRequest {

    private String eventType;
    private String paymentId;
    private String orderId;
    private Long amount;
    private String status;
    private String approvalCode;
    private String failureCode;
    private String failureMessage;
}
