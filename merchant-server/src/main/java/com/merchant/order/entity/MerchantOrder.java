package com.merchant.order.entity;

import com.merchant.order.enumtype.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "merchant_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MerchantOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_uid", nullable = false, unique = true, length = 50)
    private String orderUid;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 30)
    private OrderStatus orderStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public MerchantOrder(String orderUid, String productName, Long amount, OrderStatus orderStatus,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.orderUid = orderUid;
        this.productName = productName;
        this.amount = amount;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MerchantOrder create(String orderUid, String productName, Long amount) {
        LocalDateTime now = LocalDateTime.now();
        return MerchantOrder.builder()
                .orderUid(orderUid)
                .productName(productName)
                .amount(amount)
                .orderStatus(OrderStatus.CREATED)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void changeStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        this.updatedAt = LocalDateTime.now();
    }
}
