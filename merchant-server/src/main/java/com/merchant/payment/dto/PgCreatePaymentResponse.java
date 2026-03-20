package com.merchant.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PgCreatePaymentResponse {

    private String paymentUid;
    private String orderId;
    private String productName;
    private Long amount;
    private String status;
    private String paymentMethod;
}
