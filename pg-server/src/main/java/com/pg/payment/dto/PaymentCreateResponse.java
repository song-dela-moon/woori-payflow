package com.pg.payment.dto;

import com.pg.payment.entity.Payment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCreateResponse {
    private String paymentUid; // 발행된 TID
    private String orderId;
    private Long amount;

    public static PaymentCreateResponse of(Payment payment) {
        return PaymentCreateResponse.builder()
                .paymentUid(payment.getPaymentUid())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .build();
    }
}