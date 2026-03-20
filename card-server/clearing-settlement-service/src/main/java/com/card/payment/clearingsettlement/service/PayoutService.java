package com.card.payment.clearingsettlement.service;

import com.card.payment.clearingsettlement.client.BankTransferClient;
import com.card.payment.clearingsettlement.dto.TransferRequest;
import com.card.payment.clearingsettlement.dto.TransferResponse;
import com.card.payment.clearingsettlement.entity.*;
import com.card.payment.clearingsettlement.repository.MerchantAccountRepository;
import com.card.payment.clearingsettlement.repository.PayoutRepository;
import com.card.payment.clearingsettlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutService {

    private final SettlementRepository settlementRepository;
    private final MerchantAccountRepository merchantAccountRepository;
    private final PayoutRepository payoutRepository;
    private final BankTransferClient bankTransferClient;

    @Value("${bank.settlement.from-account}")
    private String fromAccount;

    @Transactional
    public void processCalculatedSettlements() {
        List<Settlement> settlements = settlementRepository.findByStatus(SettlementStatus.CALCULATED);

        if (settlements.isEmpty()) {
            log.info("지급 대상 settlement가 없습니다.");
            return;
        }

        for (Settlement settlement : settlements) {
            if (payoutRepository.existsBySettlementId(settlement.getId())) {
                log.warn("이미 지급 처리된 settlement입니다. settlementId={}", settlement.getId());
                continue;
            }

            MerchantAccount merchantAccount = merchantAccountRepository.findByMerchantId(settlement.getMerchantId())
                    .orElseThrow(() -> new IllegalStateException(
                            "가맹점 계좌 정보를 찾을 수 없습니다. merchantId=" + settlement.getMerchantId()));

            String payoutId = "PAYOUT-" + UUID.randomUUID().toString();


            Payout payout = Payout.builder()
                    .payoutId(payoutId)
                    .settlementId(settlement.getId())
                    .merchantId(settlement.getMerchantId())
                    .amount(settlement.getNetAmount())
                    .bankCode(merchantAccount.getBankCode())
                    .accountNumber(merchantAccount.getAccountNumber())
                    .status(PayoutStatus.REQUESTED)
                    .requestedAt(LocalDateTime.now())
                    .build();

            payoutRepository.save(payout);

            try {
                TransferRequest request = TransferRequest.builder()
                        .fromAccount(fromAccount)
                        .toAccount(merchantAccount.getAccountNumber())
                        .amount(settlement.getNetAmount())
                        .settlementId(String.valueOf(settlement.getId()))
                        .build();

                TransferResponse response = bankTransferClient.requestTransfer(request);

                if (response != null && response.isSuccess()) {
                    payout.setStatus(PayoutStatus.PAID);
                    payout.setResponseCode(response.getResponseCode());
                    payout.setResponseMessage(response.getResponseMessage());
                    payout.setPaidAt(LocalDateTime.now());

                    settlement.setStatus(SettlementStatus.PAID);

                    log.info("지급 성공 - settlementId={}, merchantId={}, amount={}",
                            settlement.getId(), settlement.getMerchantId(), settlement.getNetAmount());
                } else {
                    payout.setStatus(PayoutStatus.FAILED);
                    payout.setResponseCode(response != null ? response.getResponseCode() : "96");
                    payout.setResponseMessage(response != null ? response.getFailureReason() : "은행 응답 없음");

                    settlement.setStatus(SettlementStatus.FAILED);

                    log.error("지급 실패 - settlementId={}, merchantId={}, reason={}",
                            settlement.getId(), settlement.getMerchantId(),
                            response != null ? response.getFailureReason() : "응답 없음");
                }

            } catch (Exception e) {
                payout.setStatus(PayoutStatus.FAILED);
                payout.setResponseCode("96");
                payout.setResponseMessage(e.getMessage());

                settlement.setStatus(SettlementStatus.FAILED);

                log.error("지급 중 예외 발생 - settlementId={}, merchantId={}",
                        settlement.getId(), settlement.getMerchantId(), e);
            }

            payoutRepository.save(payout);
            settlementRepository.save(settlement);
        }
    }
}