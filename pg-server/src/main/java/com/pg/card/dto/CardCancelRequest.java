package com.pg.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardCancelRequest {

    private String paymentId;
    private String orderId;
    private Long cancelAmount;
    private String approvalCode;
    private String reason;
}