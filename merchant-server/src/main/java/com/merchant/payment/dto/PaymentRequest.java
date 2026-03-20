package com.merchant.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequest {

    private String orderUid;
    private String cardNumber;
    private String expiryYear;
    private String expiryMonth;
    private String birthOrBizNo;
    private String cardPassword2Digits;
    private Integer installmentMonths;
}
