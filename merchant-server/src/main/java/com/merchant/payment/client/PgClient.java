package com.merchant.payment.client;

import com.merchant.payment.dto.PgApiResponse;
import com.merchant.payment.dto.PgApprovePaymentRequest;
import com.merchant.payment.dto.PgApprovePaymentResponse;
import com.merchant.payment.dto.PgCreatePaymentRequest;
import com.merchant.payment.dto.PgCreatePaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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

    public PgCreatePaymentResponse createPayment(PgCreatePaymentRequest request) {
        PgApiResponse<PgCreatePaymentResponse> response = restClient.post()
                .uri(pgBaseUrl + "/api/payments/create")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (response == null || response.getData() == null) {
            throw new IllegalStateException("PG 결제 생성 응답이 비어 있습니다.");
        }

        return response.getData();
    }

    public PgApprovePaymentResponse approvePayment(PgApprovePaymentRequest request) {
        PgApiResponse<PgApprovePaymentResponse> response = restClient.post()
                .uri(pgBaseUrl + "/api/payments/approve")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (response == null || response.getData() == null) {
            throw new IllegalStateException("PG 결제 승인 응답이 비어 있습니다.");
        }

        return response.getData();
    }
}
