package com.card.payment.clearingsettlement.scheduler;

import com.card.payment.clearingsettlement.service.PayoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PayoutScheduler {

    private final PayoutService payoutService;

    @Scheduled(fixedRate = 1500) // 테스트용: 15초마다 실행
//    @Scheduled(cron = "0 10 1 * * *") // 운영용: 매일 새벽 1시 10분
    public void runPayoutJob() {
        log.info("지급 배치 시작");
        payoutService.processCalculatedSettlements();
        log.info("지급 배치 종료");
    }
}
