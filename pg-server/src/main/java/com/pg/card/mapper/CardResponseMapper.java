package com.pg.card.mapper;

import com.pg.card.dto.CardApprovalResponse;
import com.pg.card.dto.CardCancelResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CardResponseMapper {

    public CardApprovalResponse toApprovalResponse(Map<String, Object> raw) {
        return CardApprovalResponse.builder()
                .success(asBoolean(raw.get("success")))
                .code(asString(raw.get("code")))
                .message(asString(raw.get("message")))
                .approvalCode(asString(raw.get("approvalCode")))
                .approvedAt(asString(raw.get("approvedAt")))
                .build();
    }

    public CardCancelResponse toCancelResponse(Map<String, Object> raw) {
        return CardCancelResponse.builder()
                .success(asBoolean(raw.get("success")))
                .code(asString(raw.get("code")))
                .message(asString(raw.get("message")))
                .cancelCode(asString(raw.get("cancelCode")))
                .cancelledAt(asString(raw.get("cancelledAt")))
                .build();
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private boolean asBoolean(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }
}
