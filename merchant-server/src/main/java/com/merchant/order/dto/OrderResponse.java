package com.merchant.order.dto;

import com.merchant.order.entity.MerchantOrder;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponse {

    private String orderUid;
    private String productName;
    private Long amount;
    private String orderStatus;

    public static OrderResponse from(MerchantOrder order) {
        return OrderResponse.builder()
                .orderUid(order.getOrderUid())
                .productName(order.getProductName())
                .amount(order.getAmount())
                .orderStatus(order.getOrderStatus().name())
                .build();
    }
}
