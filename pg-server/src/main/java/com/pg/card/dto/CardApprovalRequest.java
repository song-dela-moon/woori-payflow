package com.pg.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardApprovalRequest {

    private String paymentId;
    private String orderId;
    private Long amount;

    private String cardNumber;
    private String expiryYear;
    private String expiryMonth;
    private String birthOrBizNo;
    private String cardPassword2Digits;

    private Integer installmentMonths;
}
