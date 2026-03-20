package com.merchant.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgPaymentRequest {

    private String merchantUid;
    private String orderId;
    private String productName;
    private Long amount;

    private String cardNumber;
    private String expiryYear;
    private String expiryMonth;
    private String birthOrBizNo;
    private String cardPassword2Digits;
    private Integer installmentMonths;

    private String apiKey;       // Added for PG creation
    private String paymentMethod; // Added for PG creation
}
