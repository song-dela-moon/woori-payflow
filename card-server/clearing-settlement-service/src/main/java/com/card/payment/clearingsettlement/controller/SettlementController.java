package com.card.payment.clearingsettlement.controller;

import com.card.payment.clearingsettlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// 정산 요청 수동 트기러용 컨트롤러
@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class    SettlementController {

    private final SettlementService settlementService;

    @PostMapping("/daily")
    public ResponseEntity<String> createDailySettlements() {
        settlementService.createDailySettlements();
        return ResponseEntity.ok("일일 정산 생성 완료");
    }
}