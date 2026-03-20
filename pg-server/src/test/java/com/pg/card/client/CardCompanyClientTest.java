package com.pg.card.client;

import com.pg.card.dto.CardApprovalRequest;
import com.pg.card.dto.CardCancelRequest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class CardCompanyClientTest {

    private MockWebServer mockWebServer;
    private CardCompanyClient cardCompanyClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        cardCompanyClient = new CardCompanyClient(WebClient.builder().build());
        ReflectionTestUtils.setField(cardCompanyClient, "baseUrl", mockWebServer.url("/").toString());
        ReflectionTestUtils.setField(cardCompanyClient, "approvePath", "approve");
        ReflectionTestUtils.setField(cardCompanyClient, "cancelPath", "cancel");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void requestApproval_postsJsonAndReturnsParsedBody() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {"success":true,"code":"0000","approvalCode":"APP-123"}
                        """));

        CardApprovalRequest request = CardApprovalRequest.builder()
                .paymentId("pay-1")
                .orderId("order-1")
                .amount(10000L)
                .cardNumber("1111-2222-3333-4444")
                .build();

        Map<String, Object> response = cardCompanyClient.requestApproval(request);

        RecordedRequest recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        assertThat(recordedRequest).isNotNull();
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
        assertThat(recordedRequest.getPath()).isEqualTo("/approve");
        assertThat(recordedRequest.getHeader("Content-Type")).contains("application/json");
        assertThat(recordedRequest.getBody().readUtf8()).contains("\"paymentId\":\"pay-1\"");
        assertThat(response).containsEntry("code", "0000");
        assertThat(response).containsEntry("approvalCode", "APP-123");
    }

    @Test
    void requestCancel_postsJsonAndReturnsParsedBody() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {"success":true,"code":"0000","cancelCode":"CAN-123"}
                        """));

        CardCancelRequest request = CardCancelRequest.builder()
                .paymentId("pay-1")
                .orderId("order-1")
                .cancelAmount(5000L)
                .approvalCode("APP-123")
                .reason("user request")
                .build();

        Map<String, Object> response = cardCompanyClient.requestCancel(request);

        RecordedRequest recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        assertThat(recordedRequest).isNotNull();
        assertThat(recordedRequest.getMethod()).isEqualTo("POST");
        assertThat(recordedRequest.getPath()).isEqualTo("/cancel");
        assertThat(recordedRequest.getHeader("Content-Type")).contains("application/json");
        assertThat(recordedRequest.getBody().readUtf8()).contains("\"cancelAmount\":5000");
        assertThat(response).containsEntry("code", "0000");
        assertThat(response).containsEntry("cancelCode", "CAN-123");
    }
}
