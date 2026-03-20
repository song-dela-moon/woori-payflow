package com.merchant.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PgPaymentResponse {

    private String paymentId;
    private String paymentUid; // Added to match PG's paymentUid (TID)
    private String status;
    private String approvalCode;
    private String failureCode;
    private String failureMessage;
}
