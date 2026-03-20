package com.merchant.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PgApprovePaymentResponse {

    private String paymentUid;
    private String orderId;
    private String productName;
    private Long amount;
    private String status;
    private String paymentMethod;
    private String approvalCode;
    private String failureCode;
    private String failureMessage;
}
