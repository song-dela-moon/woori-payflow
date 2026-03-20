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

    @Value("${card.company.approve-path:/api/cards/approve}")
    private String approvePath;

    @Value("${card.company.cancel-path:/api/cards/cancel}")
    private String cancelPath;

    public Map<String, Object> requestApproval(CardApprovalRequest request) {
        return webClient.post()
                .uri(baseUrl + approvePath)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
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
