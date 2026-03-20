package com.merchant.payment.dto;

import com.merchant.payment.entity.MerchantPayment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {

    private String orderUid;
    private String pgPaymentId;
    private Long amount;
    private String paymentStatus;
    private String approvalCode;
    private String failureCode;
    private String failureMessage;

    public static PaymentResponse from(MerchantPayment payment) {
        return PaymentResponse.builder()
                .orderUid(payment.getOrder().getOrderUid())
                .pgPaymentId(payment.getPgPaymentId())
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus().name())
                .approvalCode(payment.getApprovalCode())
                .failureCode(payment.getFailureCode())
                .failureMessage(payment.getFailureMessage())
                .build();
    }
}
