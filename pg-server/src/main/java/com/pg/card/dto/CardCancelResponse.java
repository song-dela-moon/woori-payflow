package com.pg.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardCancelResponse {

    private boolean success;
    private String code;
    private String message;

    private String cancelCode;
    private String cancelledAt;

    public boolean isFailed() {
        return !success;
    }
}