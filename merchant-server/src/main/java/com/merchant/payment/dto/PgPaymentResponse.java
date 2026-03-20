package com.merchant.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PgPaymentResponse {

    private String paymentId;
    private String status;
    private String approvalCode;
    private String failureCode;
    private String failureMessage;
}
