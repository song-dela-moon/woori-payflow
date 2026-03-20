package com.pg.card.service;

import com.pg.card.client.CardCompanyClient;
import com.pg.card.dto.CardApprovalRequest;
import com.pg.card.dto.CardApprovalResponse;
import com.pg.card.dto.CardCancelRequest;
import com.pg.card.dto.CardCancelResponse;
import com.pg.card.mapper.CardResponseMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardCompanyClient cardCompanyClient;

    @Mock
    private CardResponseMapper cardResponseMapper;

    @InjectMocks
    private CardService cardService;

    @Test
    void approve_requestsCardCompanyAndMapsResponse() {
        CardApprovalRequest request = CardApprovalRequest.builder()
                .paymentId("pay-1")
                .orderId("order-1")
                .amount(10000L)
                .build();
        Map<String, Object> rawResponse = Map.of("success", true);
        CardApprovalResponse expected = CardApprovalResponse.builder()
                .success(true)
                .code("0000")
                .build();

        when(cardCompanyClient.requestApproval(request)).thenReturn(rawResponse);
        when(cardResponseMapper.toApprovalResponse(rawResponse)).thenReturn(expected);

        CardApprovalResponse actual = cardService.approve(request);

        assertThat(actual).isSameAs(expected);
        verify(cardCompanyClient).requestApproval(request);
        verify(cardResponseMapper).toApprovalResponse(rawResponse);
    }

    @Test
    void cancel_requestsCardCompanyAndMapsResponse() {
        CardCancelRequest request = CardCancelRequest.builder()
                .paymentId("pay-1")
                .orderId("order-1")
                .cancelAmount(5000L)
                .approvalCode("APP-1")
                .reason("user request")
                .build();
        Map<String, Object> rawResponse = Map.of("success", true);
        CardCancelResponse expected = CardCancelResponse.builder()
                .success(true)
                .code("0000")
                .build();

        when(cardCompanyClient.requestCancel(request)).thenReturn(rawResponse);
        when(cardResponseMapper.toCancelResponse(rawResponse)).thenReturn(expected);

        CardCancelResponse actual = cardService.cancel(request);

        assertThat(actual).isSameAs(expected);
        verify(cardCompanyClient).requestCancel(request);
        verify(cardResponseMapper).toCancelResponse(rawResponse);
    }

    @Test
    void createApprovalRequest_populatesAllFields() {
        CardApprovalRequest request = cardService.createApprovalRequest(
                "pay-1",
                "order-1",
                10000L,
                "1234-5678-9012-3456",
                "27",
                "09",
                "900101",
                "12",
                3
        );

        assertThat(request.getPaymentId()).isEqualTo("pay-1");
        assertThat(request.getOrderId()).isEqualTo("order-1");
        assertThat(request.getAmount()).isEqualTo(10000L);
        assertThat(request.getCardNumber()).isEqualTo("1234-5678-9012-3456");
        assertThat(request.getExpiryYear()).isEqualTo("27");
        assertThat(request.getExpiryMonth()).isEqualTo("09");
        assertThat(request.getBirthOrBizNo()).isEqualTo("900101");
        assertThat(request.getCardPassword2Digits()).isEqualTo("12");
        assertThat(request.getInstallmentMonths()).isEqualTo(3);
    }

    @Test
    void createCancelRequest_populatesAllFields() {
        CardCancelRequest request = cardService.createCancelRequest(
                "pay-1",
                "order-1",
                5000L,
                "APP-1",
                "partial cancel"
        );

        assertThat(request.getPaymentId()).isEqualTo("pay-1");
        assertThat(request.getOrderId()).isEqualTo("order-1");
        assertThat(request.getCancelAmount()).isEqualTo(5000L);
        assertThat(request.getApprovalCode()).isEqualTo("APP-1");
        assertThat(request.getReason()).isEqualTo("partial cancel");
    }
}
