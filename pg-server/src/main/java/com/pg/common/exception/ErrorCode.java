package com.pg.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "C001", "올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(405, "C002", "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(500, "C003", "서버 내부 오류가 발생했습니다."),

    // Merchant (가맹점)
    MERCHANT_NOT_FOUND(404, "M001", "가맹점 정보를 찾을 수 없습니다."),
    INVALID_API_KEY(401, "M002", "유효하지 않은 API 키입니다."),
    MERCHANT_INACTIVE(403, "M003", "활성화되지 않은 가맹점입니다."),

    // Payment (결제)
    PAYMENT_NOT_FOUND(404, "P001", "결제 내역을 찾을 수 없습니다."),
    DUPLICATE_ORDER_ID(400, "P002", "이미 존재하는 주문 번호입니다."),
    INVALID_PAYMENT_AMOUNT(400, "P003", "결제 금액이 올바르지 않습니다."),
    ALREADY_APPROVED(400, "P004", "이미 승인 완료된 결제건입니다."),
    EXCEEDS_CANCELABLE_AMOUNT(400, "P005", "취소 가능 금액을 초과했습니다."),

    // External (카드사/은행)
    CARD_COMPANY_ERROR(502, "E001", "카드사 통신 중 오류가 발생했습니다."),
    CARD_REJECTED(400, "E002", "카드사에서 승인이 거절되었습니다.");

    private final int status;
    private final String code;
    private final String message;
}
