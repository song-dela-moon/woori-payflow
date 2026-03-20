package com.card.payment.authorization.messaging;

import com.card.payment.authorization.event.PaymentApprovedEvent;
import com.card.payment.authorization.event.PaymentRejectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.MimeTypeUtils;
import org.springframework.cloud.stream.function.StreamBridge;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventStreamPublisher {

    private final StreamBridge streamBridge;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentApproved(PaymentApprovedEvent event) {
        Message<PaymentApprovedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();

        boolean sent = streamBridge.send("paymentApproved-out-0", message);

        if (sent) {
            log.info("PaymentApprovedEvent 발행 성공 - transactionId={}, approvalNumber={}",
                    event.getTransactionId(), event.getApprovalNumber());
        } else {
            log.error("PaymentApprovedEvent 발행 실패 - transactionId={}", event.getTransactionId());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentRejected(PaymentRejectedEvent event) {
        Message<PaymentRejectedEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();

        boolean sent = streamBridge.send("paymentRejected-out-0", message);

        if (sent) {
            log.info("PaymentRejectedEvent 발행 성공 - transactionId={}, responseCode={}",
                    event.getTransactionId(), event.getResponseCode());
        } else {
            log.error("PaymentRejectedEvent 발행 실패 - transactionId={}", event.getTransactionId());
        }
    }
}