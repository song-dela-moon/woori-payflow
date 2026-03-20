package com.card.payment.clearingsettlement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "merchant_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String merchantId;

    @Column(nullable = false, length = 10)
    private String bankCode;

    @Column(nullable = false, length = 50)
    private String accountNumber;

    @Column(nullable = false, length = 100)
    private String accountHolder;
}