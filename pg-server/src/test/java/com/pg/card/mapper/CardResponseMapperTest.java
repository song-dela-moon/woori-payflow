package com.pg.card.mapper;

import com.pg.card.dto.CardApprovalResponse;
import com.pg.card.dto.CardCancelResponse;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CardResponseMapperTest {

    private final CardResponseMapper mapper = new CardResponseMapper();

    @Test
    void toApprovalResponse_mapsRawValues() {
        Map<String, Object> raw = Map.of(
                "success", true,
                "code", "0000",
                "message", "approved",
                "approvalCode", "APP-123",
                "approvedAt", "2026-03-20T10:15:30"
        );

        CardApprovalResponse response = mapper.toApprovalResponse(raw);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getCode()).isEqualTo("0000");
        assertThat(response.getMessage()).isEqualTo("approved");
        assertThat(response.getApprovalCode()).isEqualTo("APP-123");
        assertThat(response.getApprovedAt()).isEqualTo("2026-03-20T10:15:30");
        assertThat(response.isFailed()).isFalse();
    }

    @Test
    void toCancelResponse_handlesStringBooleanAndNulls() {
        Map<String, Object> raw = Map.of(
                "success", "false",
                "code", "C001",
                "message", "cancel failed",
                "cancelCode", "CAN-404"
        );

        CardCancelResponse response = mapper.toCancelResponse(raw);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo("C001");
        assertThat(response.getMessage()).isEqualTo("cancel failed");
        assertThat(response.getCancelCode()).isEqualTo("CAN-404");
        assertThat(response.getCancelledAt()).isNull();
        assertThat(response.isFailed()).isTrue();
    }
}
