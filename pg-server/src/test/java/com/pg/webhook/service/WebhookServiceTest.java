package com.pg.webhook.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.merchant.entity.Merchant;
import com.pg.payment.entity.Payment;
import com.pg.webhook.client.MerchantWebhookClient;
import com.pg.webhook.dto.PaymentWebhookPayload;
import com.pg.webhook.entity.WebhookHistory;
import com.pg.webhook.repository.WebhookHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    private MerchantWebhookClient merchantWebhookClient;

    @Mock
    private WebhookHistoryRepository webhookHistoryRepository;

    @Test
    void sendPaymentApprovedWebhook_sendsPayloadAndStoresSuccessHistory() {
        WebhookService webhookService = new WebhookService(
                merchantWebhookClient,
                webhookHistoryRepository,
                new ObjectMapper()
        );
        Merchant merchant = merchant("https://merchant.test/callback");
        Payment payment = approvedPayment();

        when(merchantWebhookClient.send(any(), any())).thenReturn(Mono.just(200));
        when(webhookHistoryRepository.save(any(WebhookHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(webhookService.sendPaymentApprovedWebhook(merchant, payment))
                .verifyComplete();

        ArgumentCaptor<PaymentWebhookPayload> payloadCaptor = ArgumentCaptor.forClass(PaymentWebhookPayload.class);
        verify(merchantWebhookClient).send(eq("https://merchant.test/callback"), payloadCaptor.capture());

        PaymentWebhookPayload payload = payloadCaptor.getValue();
        assertThat(payload.getEventType()).isEqualTo("PAYMENT_APPROVED");
        assertThat(payload.getPaymentId()).isEqualTo("pay-1");
        assertThat(payload.getOrderId()).isEqualTo("order-1");
        assertThat(payload.getAmount()).isEqualTo(10000L);
        assertThat(payload.getStatus()).isEqualTo("APPROVED");
        assertThat(payload.getApprovalCode()).isEqualTo("APP-123");

        ArgumentCaptor<WebhookHistory> historyCaptor = ArgumentCaptor.forClass(WebhookHistory.class);
        verify(webhookHistoryRepository).save(historyCaptor.capture());

        WebhookHistory history = historyCaptor.getValue();
        assertThat(history.isSuccess()).isTrue();
        assertThat(history.getResponseStatus()).isEqualTo(200);
        assertThat(history.getEventType()).isEqualTo("PAYMENT_APPROVED");
        assertThat(history.getCallbackUrl()).isEqualTo("https://merchant.test/callback");
        assertThat(history.getRequestBody()).contains("\"eventType\":\"PAYMENT_APPROVED\"");
    }

    @Test
    void sendPaymentFailedWebhook_storesFailureHistoryWhenWebhookCallFails() {
        WebhookService webhookService = new WebhookService(
                merchantWebhookClient,
                webhookHistoryRepository,
                new ObjectMapper()
        );
        Merchant merchant = merchant("https://merchant.test/callback");
        Payment payment = failedPayment();

        when(merchantWebhookClient.send(any(), any())).thenReturn(Mono.error(new RuntimeException("callback failed")));
        when(webhookHistoryRepository.save(any(WebhookHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(webhookService.sendPaymentFailedWebhook(merchant, payment))
                .verifyComplete();

        ArgumentCaptor<PaymentWebhookPayload> payloadCaptor = ArgumentCaptor.forClass(PaymentWebhookPayload.class);
        verify(merchantWebhookClient).send(eq("https://merchant.test/callback"), payloadCaptor.capture());

        PaymentWebhookPayload payload = payloadCaptor.getValue();
        assertThat(payload.getEventType()).isEqualTo("PAYMENT_FAILED");
        assertThat(payload.getStatus()).isEqualTo("FAILED");
        assertThat(payload.getFailureCode()).isEqualTo("CARD_DECLINED");
        assertThat(payload.getFailureMessage()).isEqualTo("insufficient funds");

        ArgumentCaptor<WebhookHistory> historyCaptor = ArgumentCaptor.forClass(WebhookHistory.class);
        verify(webhookHistoryRepository).save(historyCaptor.capture());

        WebhookHistory history = historyCaptor.getValue();
        assertThat(history.isSuccess()).isFalse();
        assertThat(history.getResponseStatus()).isNull();
        assertThat(history.getEventType()).isEqualTo("PAYMENT_FAILED");
        assertThat(history.getRequestBody()).contains("\"failureCode\":\"CARD_DECLINED\"");
    }

    @Test
    void sendPaymentCancelledWebhook_usesCancelAmountInPayload() {
        WebhookService webhookService = new WebhookService(
                merchantWebhookClient,
                webhookHistoryRepository,
                new ObjectMapper()
        );
        Merchant merchant = merchant("https://merchant.test/callback");
        Payment payment = approvedPayment();

        when(merchantWebhookClient.send(any(), any())).thenReturn(Mono.just(202));
        when(webhookHistoryRepository.save(any(WebhookHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StepVerifier.create(webhookService.sendPaymentCancelledWebhook(merchant, payment, 3000L))
                .verifyComplete();

        ArgumentCaptor<PaymentWebhookPayload> payloadCaptor = ArgumentCaptor.forClass(PaymentWebhookPayload.class);
        verify(merchantWebhookClient).send(eq("https://merchant.test/callback"), payloadCaptor.capture());

        PaymentWebhookPayload payload = payloadCaptor.getValue();
        assertThat(payload.getEventType()).isEqualTo("PAYMENT_CANCELLED");
        assertThat(payload.getStatus()).isEqualTo("CANCELLED");
        assertThat(payload.getAmount()).isEqualTo(3000L);
        assertThat(payload.getApprovalCode()).isEqualTo("APP-123");
    }

    private Merchant merchant(String callbackUrl) {
        Merchant merchant = instantiate(Merchant.class);
        ReflectionTestUtils.setField(merchant, "callbackUrl", callbackUrl);
        return merchant;
    }

    private Payment approvedPayment() {
        Payment payment = instantiate(Payment.class);
        ReflectionTestUtils.setField(payment, "paymentUid", "pay-1");
        ReflectionTestUtils.setField(payment, "orderId", "order-1");
        ReflectionTestUtils.setField(payment, "amount", 10000L);
        ReflectionTestUtils.setField(payment, "approvalCode", "APP-123");
        return payment;
    }

    private Payment failedPayment() {
        Payment payment = approvedPayment();
        ReflectionTestUtils.setField(payment, "approvalCode", null);
        ReflectionTestUtils.setField(payment, "failureCode", "CARD_DECLINED");
        ReflectionTestUtils.setField(payment, "failureMessage", "insufficient funds");
        return payment;
    }

    private <T> T instantiate(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate " + type.getSimpleName(), e);
        }
    }
}
