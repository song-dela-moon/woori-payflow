package com.pg.webhook.service;

import com.pg.merchant.entity.Merchant;
import com.pg.payment.entity.Payment;
import com.pg.webhook.client.MerchantWebhookClient;
import com.pg.webhook.dto.PaymentWebhookPayload;
import com.pg.webhook.entity.WebhookHistory;
import com.pg.webhook.repository.WebhookHistoryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final MerchantWebhookClient merchantWebhookClient;
    private final WebhookHistoryRepository webhookHistoryRepository;
    private final ObjectMapper objectMapper;

    public Mono<Void> sendPaymentApprovedWebhook(Merchant merchant, Payment payment) {
        PaymentWebhookPayload payload = PaymentWebhookPayload.builder()
                .eventType("PAYMENT_APPROVED")
                .paymentId(payment.getPaymentUid())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status("APPROVED")
                .approvalCode(payment.getApprovalCode())
                .failureCode(null)
                .failureMessage(null)
                .build();

        return sendWebhook(merchant, payment, payload);
    }

    public Mono<Void> sendPaymentFailedWebhook(Merchant merchant, Payment payment) {
        PaymentWebhookPayload payload = PaymentWebhookPayload.builder()
                .eventType("PAYMENT_FAILED")
                .paymentId(payment.getPaymentUid())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status("FAILED")
                .approvalCode(null)
                .failureCode(payment.getFailureCode())
                .failureMessage(payment.getFailureMessage())
                .build();

        return sendWebhook(merchant, payment, payload);
    }

    public Mono<Void> sendPaymentCancelledWebhook(Merchant merchant, Payment payment, Long cancelAmount) {
        PaymentWebhookPayload payload = PaymentWebhookPayload.builder()
                .eventType("PAYMENT_CANCELLED")
                .paymentId(payment.getPaymentUid())
                .orderId(payment.getOrderId())
                .amount(cancelAmount)
                .status("CANCELLED")
                .approvalCode(payment.getApprovalCode())
                .failureCode(null)
                .failureMessage(null)
                .build();

        return sendWebhook(merchant, payment, payload);
    }

    private Mono<Void> sendWebhook(Merchant merchant, Payment payment, PaymentWebhookPayload payload) {
        String callbackUrl = merchant.getCallbackUrl();
        String requestBody = toJson(payload);

        return merchantWebhookClient.send(callbackUrl, payload)
                .flatMap(statusCode -> {
                    return saveWebhookHistoryAsync(
                            merchant,
                            payment,
                            payload.getEventType(),
                            callbackUrl,
                            requestBody,
                            statusCode,
                            statusCode >= 200 && statusCode < 300
                    );
                })
                .onErrorResume(ex -> {
                    return saveWebhookHistoryAsync(
                            merchant,
                            payment,
                            payload.getEventType(),
                            callbackUrl,
                            requestBody,
                            null,
                            false
                    );
                });
    }

    private Mono<Void> saveWebhookHistoryAsync(
            Merchant merchant,
            Payment payment,
            String eventType,
            String callbackUrl,
            String requestBody,
            Integer responseStatus,
            boolean success
    ) {
        return Mono.fromRunnable(() -> saveWebhookHistory(
                        merchant,
                        payment,
                        eventType,
                        callbackUrl,
                        requestBody,
                        responseStatus,
                        success
                ))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private void saveWebhookHistory(
            Merchant merchant,
            Payment payment,
            String eventType,
            String callbackUrl,
            String requestBody,
            Integer responseStatus,
            boolean success
    ) {
        WebhookHistory webhookHistory = WebhookHistory.builder()
                .merchant(merchant)
                .payment(payment)
                .eventType(eventType)
                .callbackUrl(callbackUrl)
                .requestBody(requestBody)
                .responseStatus(responseStatus)
                .success(success)
                .sentAt(LocalDateTime.now())
                .build();

        webhookHistoryRepository.save(webhookHistory);
    }

    private String toJson(PaymentWebhookPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"payload serialization failed\"}";
        }
    }
}
