package com.pg.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardApprovalRequest {

    // 1. 카드사 전송용 (명세 일치)
    private String transactionId;
    private String merchantId;
    private BigDecimal amount;
    private String terminalId;
    private String pin;

    // 2. 기존 로직 유지용 (오류 방지)
    private String paymentId;
    private String orderId;
    private String merchantUid;

    private String cardNumber;
    private String expiryYear;
    private String expiryMonth;
    private String birthOrBizNo;
    private String cardPassword2Digits;
    private Integer installmentMonths;

    private Long originalAmount;
}