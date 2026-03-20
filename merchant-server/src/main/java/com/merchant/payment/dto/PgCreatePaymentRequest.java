package com.merchant.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgCreatePaymentRequest {

    private String merchantUid;
    private String orderId;
    private String productName;
    private Long amount;
    private String paymentMethod;
    private String apiKey;
}
