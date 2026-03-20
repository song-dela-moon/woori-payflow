package com.pg.webhook.client;

import com.pg.webhook.dto.PaymentWebhookPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MerchantWebhookClient {

    private final WebClient webClient;

    public Mono<Integer> send(String callbackUrl, PaymentWebhookPayload payload) {
        return webClient.post()
                .uri(callbackUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchangeToMono(response -> Mono.just(response.statusCode().value()));
    }
}
