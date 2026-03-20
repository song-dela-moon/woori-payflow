package com.card.payment.clearingsettlement.client;

import com.card.payment.clearingsettlement.dto.TransferRequest;
import com.card.payment.clearingsettlement.dto.TransferResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BankTransferClientImpl implements BankTransferClient {

    private final RestClient restClient;

    public BankTransferClientImpl(@Value("${bank.service.url}") String bankServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(bankServiceUrl)
                .build();
    }

    @Override
    public TransferResponse requestTransfer(TransferRequest request) {
        return restClient.post()
                .uri("/api/transfer/request")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(TransferResponse.class);
    }
}