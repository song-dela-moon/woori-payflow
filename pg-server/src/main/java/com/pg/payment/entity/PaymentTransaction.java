package com.pg.payment.entity;

import com.pg.payment.enumtype.ResultType;
import com.pg.payment.enumtype.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String transactionUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // APPROVE, CANCEL

    private Long amount;

    @Enumerated(EnumType.STRING)
    private ResultType resultType; // SUCCESS, FAIL

    private String externalCode; // 카드사 응답코드
    private String externalMessage;
    private String approvalCode;

    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;
    private LocalDateTime createdAt;
}