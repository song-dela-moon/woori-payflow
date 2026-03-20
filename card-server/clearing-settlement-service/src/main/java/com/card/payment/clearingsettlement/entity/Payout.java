package com.card.payment.clearingsettlement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payouts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String payoutId;

    @Column(nullable = false)
    private Long settlementId;

    @Column(nullable = false, length = 50)
    private String merchantId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    private String bankCode;

    @Column(nullable = false, length = 50)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayoutStatus status;

    @Column(length = 20)
    private String responseCode;

    @Column(length = 255)
    private String responseMessage;

    private LocalDateTime requestedAt;
    private LocalDateTime paidAt;
}