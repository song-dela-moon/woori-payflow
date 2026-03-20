package com.pg.payment.enumtype;

public enum PaymentStatus {
    READY,      // 결제 생성됨 (TID 발행)
    PENDING,    // 카드사 승인 대기 중
    SUCCESS,    // 결제 성공
    FAIL,       // 결제 실패
    CANCELED,   // 전액 취소
    PARTIAL_CANCELED; // 부분 취소
}