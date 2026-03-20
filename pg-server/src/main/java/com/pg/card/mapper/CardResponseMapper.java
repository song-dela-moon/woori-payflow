package com.pg.card.mapper;

import com.pg.card.dto.CardApprovalResponse;
import com.pg.card.dto.CardCancelResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CardResponseMapper {

    public CardApprovalResponse toApprovalResponse(Map<String, Object> raw) {
        log.info("Mapping Card Approval Response: {}", raw);
        return CardApprovalResponse.builder()
                .success(asBoolean(raw.get("approved"))) // Card Service uses 'approved'
                .code(asString(raw.get("responseCode"))) // Card Service uses 'responseCode'
                .message(asString(raw.get("message")))
                .approvalCode(asString(raw.get("approvalNumber"))) // Card Service uses 'approvalNumber'
                .approvedAt(asString(raw.get("authorizationDate"))) // Card Service uses 'authorizationDate'
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
