package com.merchant.payment.client;

import com.merchant.payment.dto.PgPaymentRequest;
import com.merchant.payment.dto.PgPaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PgClient {

    private final RestClient restClient;

    @Value("${pg.base-url}")
    private String pgBaseUrl;

    public PgClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public PgPaymentResponse requestPaymentApproval(PgPaymentRequest request) {
        return restClient.post()
                .uri(pgBaseUrl + "/api/payments/approve")
                .body(request)
                .retrieve()
                .body(PgPaymentResponse.class);
    }
}
