package com.card.payment.clearingsettlement.scheduler;

import com.card.payment.clearingsettlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementScheduler {

    private final SettlementService settlementService;

    @Scheduled(fixedRate = 10000)   // 테스트용: 10초마다 실행
//    @Scheduled(cron = "0 0 1 * * *")  // 운영용: 매일 새벽 1시
    public void runSettlementJob() {
        log.info("자동 정산 배치 시작");
        settlementService.createDailySettlements();
        log.info("자동 정산 배치 종료");
    }
}
