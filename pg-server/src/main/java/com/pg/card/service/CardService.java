package com.pg.card.service;

import com.pg.card.client.CardCompanyClient;
import com.pg.card.dto.CardApprovalRequest;
import com.pg.card.dto.CardApprovalResponse;
import com.pg.card.dto.CardCancelRequest;
import com.pg.card.dto.CardCancelResponse;
import com.pg.card.mapper.CardResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            Integer installmentMonths
    ) {
        return CardApprovalRequest.builder()
                .paymentId(paymentId)
                .orderId(orderId)
                .amount(amount)
                .cardNumber(cardNumber)
                .expiryYear(expiryYear)
                .expiryMonth(expiryMonth)
                .birthOrBizNo(birthOrBizNo)
                .cardPassword2Digits(cardPassword2Digits)
                .installmentMonths(installmentMonths)
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
