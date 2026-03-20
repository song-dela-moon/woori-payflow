package com.merchant.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    private String productName;
    private Long amount;
}
