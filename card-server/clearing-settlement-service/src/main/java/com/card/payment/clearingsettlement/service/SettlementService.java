package com.card.payment.clearingsettlement.service;

import com.card.payment.clearingsettlement.entity.ClearingStatus;
import com.card.payment.clearingsettlement.entity.ClearingTransaction;
import com.card.payment.clearingsettlement.entity.Settlement;
import com.card.payment.clearingsettlement.entity.SettlementStatus;
import com.card.payment.clearingsettlement.repository.ClearingTransactionRepository;
import com.card.payment.clearingsettlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementService {

    private final ClearingTransactionRepository clearingTransactionRepository;
    private final SettlementRepository settlementRepository;

    private static final BigDecimal FEE_RATE = new BigDecimal("0.03");

    @Transactional
    public void createDailySettlements() {
        List<ClearingTransaction> targets =
                clearingTransactionRepository.findByStatus(ClearingStatus.RECEIVED);

        if (targets.isEmpty()) {
            log.info("정산 대상 거래가 없습니다.");
            return;
        }

        Map<String, List<ClearingTransaction>> byMerchant =
                targets.stream().collect(Collectors.groupingBy(ClearingTransaction::getMerchantId));

        LocalDate businessDate = LocalDate.now();

        for (Map.Entry<String, List<ClearingTransaction>> entry : byMerchant.entrySet()) {
            String merchantId = entry.getKey();
            List<ClearingTransaction> transactions = entry.getValue();

//            if (settlementRepository.findByMerchantIdAndBusinessDate(merchantId, businessDate).isPresent()) {
//                log.warn("이미 정산이 생성된 merchantId={}, businessDate={}", merchantId, businessDate);
//                continue;
//            }

            BigDecimal grossAmount = transactions.stream()
                    .map(ClearingTransaction::getEffectiveAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal feeAmount = grossAmount.multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP);
            BigDecimal netAmount = grossAmount.subtract(feeAmount);

            Settlement settlement = Settlement.builder()
                    .merchantId(merchantId)
                    .businessDate(businessDate)
                    .grossAmount(grossAmount)
                    .feeAmount(feeAmount)
                    .netAmount(netAmount)
                    .status(SettlementStatus.CALCULATED)
                    .build();

            settlementRepository.save(settlement);

            transactions.forEach(tx -> tx.setStatus(ClearingStatus.SETTLED));
            clearingTransactionRepository.saveAll(transactions);

            log.info("정산 생성 완료 - merchantId={}, gross={}, fee={}, net={}",
                    merchantId, grossAmount, feeAmount, netAmount);
        }
    }
}