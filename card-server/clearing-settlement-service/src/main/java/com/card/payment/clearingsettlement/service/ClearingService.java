package com.card.payment.clearingsettlement.service;

import com.card.payment.clearingsettlement.dto.PaymentApprovedEvent;
import com.card.payment.clearingsettlement.entity.ClearingStatus;
import com.card.payment.clearingsettlement.entity.ClearingTransaction;
import com.card.payment.clearingsettlement.repository.ClearingTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClearingService {

    private final ClearingTransactionRepository clearingTransactionRepository;

    public void saveApprovedTransaction(PaymentApprovedEvent event) {
        if (clearingTransactionRepository.existsByEventId(event.getEventId())) {
            log.warn("이미 처리된 이벤트입니다. eventId={}", event.getEventId());
            return;
        }

        ClearingTransaction transaction = ClearingTransaction.builder()
                .eventId(event.getEventId())
                .transactionId(event.getTransactionId())
                .approvalNumber(event.getApprovalNumber())
                .merchantId(event.getMerchantId())
                .amount(event.getAmount())
                .effectiveAmount(event.getAmount())
                .status(ClearingStatus.RECEIVED)
                .approvedAt(event.getApprovedAt())
                .build();

        clearingTransactionRepository.save(transaction);
        log.info("ClearingTransaction 저장 완료 - transactionId={}", event.getTransactionId());
    }
}