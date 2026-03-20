package com.merchant.payment.client;

import com.merchant.payment.dto.PgApiResponse;
import com.merchant.payment.dto.PgPaymentRequest;
import com.merchant.payment.dto.PgPaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class PgClient {

    private final RestClient restClient;

    @Value("${pg.base-url}")
    private String pgBaseUrl;

    public PgClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public PgPaymentResponse createPayment(PgPaymentRequest request) {
        // PG사의 결제 생성(TID 발급) API 호출
        PgApiResponse<PgPaymentResponse> response = restClient.post()
                .uri(pgBaseUrl + "/api/payments/create")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<PgApiResponse<PgPaymentResponse>>() {});
        
        return response != null ? response.getData() : null;
    }

    public PgPaymentResponse requestPaymentApproval(PgPaymentRequest request, String paymentUid) {
        // PG사의 결제 승인 API 호출 (TID 포함)
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("paymentUid", paymentUid);
        body.put("cardNumber", request.getCardNumber());
        body.put("expiryYear", request.getExpiryYear());
        body.put("expiryMonth", request.getExpiryMonth());
        body.put("birthOrBizNo", request.getBirthOrBizNo());
        body.put("cardPassword2Digits", request.getCardPassword2Digits());
        body.put("installmentMonths", request.getInstallmentMonths() != null ? request.getInstallmentMonths() : 0);

        PgApiResponse<PgPaymentResponse> response = restClient.post()
                .uri(pgBaseUrl + "/api/payments/approve")
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<PgApiResponse<PgPaymentResponse>>() {});

        return response != null ? response.getData() : null;
    }
}
