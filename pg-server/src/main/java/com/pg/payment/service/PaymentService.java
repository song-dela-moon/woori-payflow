package com.pg.payment.service;

import com.pg.card.dto.CardApprovalRequest;
import com.pg.card.dto.CardApprovalResponse;
import com.pg.card.dto.CardCancelRequest;
import com.pg.card.dto.CardCancelResponse;
import com.pg.card.service.CardService;
import com.pg.merchant.service.MerchantService;
import com.pg.payment.dto.PaymentApproveRequest;
import com.pg.payment.dto.PaymentCancelRequest;
import com.pg.payment.dto.PaymentResponse;
import com.pg.payment.entity.Payment;
import com.pg.webhook.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentQueryService paymentQueryService;
    private final PaymentCommandService paymentCommandService;
    private final CardService cardService;
    private final WebhookService webhookService;
    private final MerchantService merchantService;

    /**
     * 최종 결제 승인 프로세스
     */
    public PaymentResponse approvePayment(PaymentApproveRequest request) {
        // 1. 결제 원장 조회 (READY 상태 확인)
        Payment payment = paymentQueryService.getPaymentByUid(request.getPaymentUid());

        // 2. 카드사 승인 요청 데이터 생성
        CardApprovalRequest cardRequest = cardService.createApprovalRequest(
                payment.getPaymentUid(),
                payment.getOrderId(),
                payment.getAmount(),
                request.getCardNumber(),
                request.getExpiryYear(),
                request.getExpiryMonth(),
                request.getBirthOrBizNo(),
                request.getCardPassword2Digits(),
                request.getInstallmentMonths()
        );

        try {
            // 3. 카드사 승인 실행
            CardApprovalResponse cardResponse = cardService.approve(cardRequest);
//            CardApprovalResponse cardResponse = CardApprovalResponse.builder()
//                    .success(true)
//                    .approvalCode("TEST-APP-12345")
//                    .code("0000")
//                    .message("테스트 승인 성공")
//                    .build();

            if (cardResponse.isSuccess()) {
                // 4-1. 성공 처리 (DB 업데이트)
                paymentCommandService.updateToSuccess(payment, cardResponse);

                // 5-1. 웹훅 발송 (비동기 처리)
                webhookService.sendPaymentApprovedWebhook(payment.getMerchant(), payment).subscribe();

                return PaymentResponse.from(payment);
            } else {
                // 4-2. 카드사 거절 처리
                paymentCommandService.updateToFail(payment, cardResponse.getCode(), cardResponse.getMessage());

                // 5-2. 웹훅 발송 (비동기 처리)
                webhookService.sendPaymentFailedWebhook(payment.getMerchant(), payment).subscribe();

                return PaymentResponse.from(payment);
            }

        } catch (Exception e) {
            log.error("결제 승인 중 예상치 못한 오류 발생: {}", e.getMessage());
            paymentCommandService.updateToFail(payment, "SYSTEM_ERROR", "Internal Server Error");
            throw e;
        }
    }

    @Transactional
    public PaymentResponse cancelPayment(PaymentCancelRequest request) {
        // 1. 가맹점 및 API Key 검증
        merchantService.validateMerchant(request.getMerchantUid(), request.getApiKey());

        // 2. 원본 결제 데이터 조회
        Payment payment = paymentQueryService.getPaymentByUid(request.getPaymentUid());

        // 3. 카드사 취소 요청 객체 생성 (팀원 CardService 활용)
        CardCancelRequest cardReq = cardService.createCancelRequest(
                payment.getPaymentUid(),
                payment.getOrderId(),
                request.getCancelAmount(),
                payment.getApprovalCode(),
                request.getReason()
        );

        try {
            // 4. 카드사 취소 실행
            CardCancelResponse cardRes = cardService.cancel(cardReq);

//            CardCancelResponse cardRes = CardCancelResponse.builder()
//                    .success(true)
//                    .cancelCode("CANCEL-12345")
//                    .code("0000")
//                    .message("테스트 취소 성공")
//                    .build();

            if (cardRes.isSuccess()) {
                // 5-1. 취소 성공 시 DB 업데이트
                paymentCommandService.updateToCancelSuccess(payment, request.getCancelAmount(), cardRes.getCancelCode());

                // 6-1. 가맹점 웹훅 통보 (비동기)
                webhookService.sendPaymentCancelledWebhook(payment.getMerchant(), payment, request.getCancelAmount()).subscribe();
            } else {
                // 5-2. 취소 실패 시 이력만 기록
                paymentCommandService.updateToCancelFail(payment, request.getCancelAmount(), cardRes.getCode(), cardRes.getMessage());
            }

            return PaymentResponse.from(payment);

        } catch (Exception e) {
            paymentCommandService.updateToCancelFail(payment, request.getCancelAmount(), "SYSTEM_ERROR", e.getMessage());
            throw e;
        }
    }
}