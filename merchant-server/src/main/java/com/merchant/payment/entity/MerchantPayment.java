package com.merchant.payment.entity;

import com.merchant.order.entity.MerchantOrder;
import com.merchant.payment.enumtype.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "merchant_payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MerchantPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "order_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private MerchantOrder order;

    @Column(name = "pg_payment_id", length = 50)
    private String pgPaymentId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus;

    @Column(name = "approval_code", length = 50)
    private String approvalCode;

    @Column(name = "failure_code", length = 50)
    private String failureCode;

    @Column(name = "failure_message", length = 255)
    private String failureMessage;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public MerchantPayment(
            MerchantOrder order,
            String pgPaymentId,
            Long amount,
            PaymentStatus paymentStatus,
            String approvalCode,
            String failureCode,
            String failureMessage,
            LocalDateTime requestedAt,
            LocalDateTime respondedAt,
            LocalDateTime createdAt
    ) {
        this.order = order;
        this.pgPaymentId = pgPaymentId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.approvalCode = approvalCode;
        this.failureCode = failureCode;
        this.failureMessage = failureMessage;
        this.requestedAt = requestedAt;
        this.respondedAt = respondedAt;
        this.createdAt = createdAt;
    }

    public static MerchantPayment create(MerchantOrder order) {
        LocalDateTime now = LocalDateTime.now();
        return MerchantPayment.builder()
                .order(order)
                .amount(order.getAmount())
                .paymentStatus(PaymentStatus.READY)
                .requestedAt(now)
                .createdAt(now)
                .build();
    }

    public void markRequested() {
        this.paymentStatus = PaymentStatus.REQUESTED;
        this.requestedAt = LocalDateTime.now();
    }

    public void markRequested(String pgPaymentId) {
        this.pgPaymentId = pgPaymentId;
        markRequested();
    }

    public void markApproved(String pgPaymentId, String approvalCode) {
        this.pgPaymentId = pgPaymentId;
        this.approvalCode = approvalCode;
        this.paymentStatus = PaymentStatus.APPROVED;
        this.respondedAt = LocalDateTime.now();
    }

    public void markFailed(String pgPaymentId, String failureCode, String failureMessage) {
        this.pgPaymentId = pgPaymentId;
        this.failureCode = failureCode;
        this.failureMessage = failureMessage;
        this.paymentStatus = PaymentStatus.FAILED;
        this.respondedAt = LocalDateTime.now();
    }

    public void markCanceled(String pgPaymentId) {
        this.pgPaymentId = pgPaymentId;
        this.paymentStatus = PaymentStatus.CANCELED;
        this.respondedAt = LocalDateTime.now();
    }
}
