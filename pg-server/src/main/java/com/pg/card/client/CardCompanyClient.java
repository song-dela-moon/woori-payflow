package com.pg.card.client;

import com.pg.card.dto.CardApprovalRequest;
import com.pg.card.dto.CardCancelRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CardCompanyClient {

    private final WebClient webClient;

    @Value("${card.company.base-url}")
    private String baseUrl;

    @Value("${card.company.approve-path:/api/authorization/request}")
    private String approvePath;

    @Value("${card.company.cancel-path:/api/cards/cancel}")
    private String cancelPath;

    public Map<String, Object> requestApproval(CardApprovalRequest request) {
        // 카드 승인 서비스의 AuthorizationRequest 형식으로 변환하여 전송
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("transactionId", request.getPaymentId());
        body.put("cardNumber", request.getCardNumber());
        body.put("amount", request.getAmount());
        body.put("merchantId", request.getMerchantUid());
        body.put("pin", request.getCardPassword2Digits() != null ? request.getCardPassword2Digits() : "1234");
        body.put("installmentMonths", request.getInstallmentMonths() != null ? request.getInstallmentMonths() : 0);

        return webClient.post()
                .uri(baseUrl + approvePath)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    public Map<String, Object> requestCancel(CardCancelRequest request) {
        return webClient.post()
                .uri(baseUrl + cancelPath)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
}
