package com.card.payment.clearingsettlement.consumer;

import com.card.payment.clearingsettlement.dto.PaymentApprovedEvent;
import com.card.payment.clearingsettlement.service.ClearingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PaymentApprovedEventConsumer {

    private final ClearingService clearingService;

    @Bean
    public Consumer<PaymentApprovedEvent> paymentApproved() {
        return event -> {
            log.info("PaymentApprovedEvent 수신 - transactionId={}", event.getTransactionId());
            clearingService.saveApprovedTransaction(event);
        };
    }
}