package com.pg.webhook.client;

import com.pg.webhook.dto.PaymentWebhookPayload;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class MerchantWebhookClientTest {

    private MockWebServer mockWebServer;
    private MerchantWebhookClient merchantWebhookClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        merchantWebhookClient = new MerchantWebhookClient(WebClient.builder().build());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void send_postsPayloadAndReturnsStatusCode() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(204));

        PaymentWebhookPayload payload = PaymentWebhookPayload.builder()
                .eventType("PAYMENT_APPROVED")
                .paymentId("pay-1")
                .orderId("order-1")
                .amount(10000L)
                .status("APPROVED")
                .approvalCode("APP-123")
                .build();

        StepVerifier.create(merchantWebhookClient.send(mockWebServer.url("/callback").toString(), payload))
                .expectNext(204)
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        assertThat(recordedRequest).isNotNull();
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
        assertThat(recordedRequest.getPath()).isEqualTo("/callback");
        assertThat(recordedRequest.getHeader("Content-Type")).contains("application/json");
        assertThat(recordedRequest.getBody().readUtf8()).contains("\"eventType\":\"PAYMENT_APPROVED\"");
    }
}
