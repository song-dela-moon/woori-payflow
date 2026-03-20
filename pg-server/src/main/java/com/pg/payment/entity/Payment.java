package com.pg.payment.entity;

import com.pg.merchant.entity.Merchant;
import com.pg.payment.enumtype.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 외부에서 생성자 직접 호출 방지
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uq_payment_merchant_order", columnNames = {"merchant_id", "order_id"})
})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String paymentUid; // PG사가 생성하는 TID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    private String orderId; // 가맹점 주문번호
    private String productName;
    private Long amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String paymentMethod;
    private String approvalCode;
    private String failureCode;
    private String failureMessage;

    @Builder.Default // Builder 사용 시에도 초기값 0L이 들어가도록 보장
    private Long cancelledAmount = 0L;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @CreatedDate // 데이터 생성 시 자동으로 현재 시간 주입
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // 수정 시 자동으로 현재 시간 주입
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 비즈니스 로직: 결제 승인 성공 시 업데이트
     */
    public void approve(String approvalCode) {
        this.status = PaymentStatus.SUCCESS;
        this.approvalCode = approvalCode;
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 비즈니스 로직: 결제 승인 실패 시 업데이트
     */
    public void fail(String failureCode, String failureMessage) {
        this.status = PaymentStatus.FAIL;
        this.failureCode = failureCode;
        this.failureMessage = failureMessage;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 비즈니스 로직: 결제 취소 시 업데이트 (부분 취소 고려)
     */
    public void cancel(Long cancelAmount) {
        this.cancelledAmount += cancelAmount;
        if (this.cancelledAmount.equals(this.amount)) {
            this.status = PaymentStatus.CANCELED;
        } else {
            this.status = PaymentStatus.PARTIAL_CANCELED;
        }
        this.updatedAt = LocalDateTime.now();
    }
}