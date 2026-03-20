package com.pg.payment.dto;

import com.pg.payment.entity.Payment;
import com.pg.payment.enumtype.PaymentStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResponse {

    private String paymentUid;       // PG 거래 고유 번호 (TID)
    private String orderId;          // 가맹점 주문 번호
    private String productName;      // 상품명
    private Long amount;             // 결제 금액
    private PaymentStatus status;    // 최종 결제 상태 (SUCCESS, FAIL 등)
    private String paymentMethod;    // 결제 수단

    private String approvalCode;     // 카드사 승인 번호 (성공 시)
    private String failureCode;      // 실패 코드 (실패 시)
    private String failureMessage;   // 실패 메시지 (실패 시)

    private LocalDateTime approvedAt; // 승인 일시
    private LocalDateTime createdAt;  // 요청 일시

    /**
     * Entity -> DTO 변환 메서드
     */
    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .paymentUid(payment.getPaymentUid())
                .orderId(payment.getOrderId())
                .productName(payment.getProductName())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .approvalCode(payment.getApprovalCode())
                .failureCode(payment.getFailureCode())
                .failureMessage(payment.getFailureMessage())
                .approvedAt(payment.getApprovedAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}