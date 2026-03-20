package com.pg.card.service;

import com.pg.card.client.CardCompanyClient;
import com.pg.card.dto.CardApprovalRequest;
import com.pg.card.dto.CardApprovalResponse;
import com.pg.card.dto.CardCancelRequest;
import com.pg.card.dto.CardCancelResponse;
import com.pg.card.mapper.CardResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal; // 추가
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardCompanyClient cardCompanyClient;
    private final CardResponseMapper cardResponseMapper;

    public CardApprovalResponse approve(CardApprovalRequest request) {
        Map<String, Object> rawResponse = cardCompanyClient.requestApproval(request);
        return cardResponseMapper.toApprovalResponse(rawResponse);
    }

    public CardCancelResponse cancel(CardCancelRequest request) {
        Map<String, Object> rawResponse = cardCompanyClient.requestCancel(request);
        return cardResponseMapper.toCancelResponse(rawResponse);
    }
    public CardApprovalRequest createApprovalRequest(
            String paymentId,
            String orderId,
            Long amount,
            String cardNumber,
            String expiryYear,
            String expiryMonth,
            String birthOrBizNo,
            String cardPassword2Digits,
            Integer installmentMonths,
            String merchantUid
    ) {
        String cleanCardNumber = (cardNumber != null) ? cardNumber.replaceAll("-", "") : "";

        return CardApprovalRequest.builder()
                // 카드사 명세 매핑: paymentId -> transactionId
                .transactionId(paymentId)
                // 카드사 명세 매핑: Long -> BigDecimal
                .amount(amount != null ? BigDecimal.valueOf(amount) : BigDecimal.ZERO)
                // 카드사 명세 매핑: 가맹점 ID (기존 orderId나 별도 값을 merchantId로 활용)
                .merchantId("MERCHANT-001") // 실제 연동 시에는 가맹점 식별자를 넘겨야 함
                .cardNumber(cleanCardNumber)
                // 카드사 명세 필드 (기본값 세팅)
                .terminalId("TERMINAL-001")
                .pin(cardPassword2Digits) // PIN으로 활용

                .paymentId(paymentId)
                .orderId(orderId)
                .expiryYear(expiryYear)
                .expiryMonth(expiryMonth)
                .birthOrBizNo(birthOrBizNo)
                .cardPassword2Digits(cardPassword2Digits)
                .installmentMonths(installmentMonths)
                .merchantUid(merchantUid)
                .build();
    }

    public CardCancelRequest createCancelRequest(
            String paymentId,
            String orderId,
            Long cancelAmount,
            String approvalCode,
            String reason
    ) {
        return CardCancelRequest.builder()
                .paymentId(paymentId)
                .orderId(orderId)
                .cancelAmount(cancelAmount)
                .approvalCode(approvalCode)
                .reason(reason)
                .build();
    }
}