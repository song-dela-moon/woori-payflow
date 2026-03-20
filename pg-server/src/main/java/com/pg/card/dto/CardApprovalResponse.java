package com.pg.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardApprovalResponse {

    private boolean success;
    private String code;
    private String message;

    private String approvalCode;
    private String approvedAt;

    public boolean isFailed() {
        return !success;
    }

    public Long getAmount() {
        return null;
    }
}