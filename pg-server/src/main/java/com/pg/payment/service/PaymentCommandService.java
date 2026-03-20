package com.pg.payment.service;

import com.pg.card.dto.CardApprovalResponse;
import com.pg.common.exception.BusinessException;
import com.pg.common.exception.ErrorCode;
import com.pg.merchant.entity.Merchant;
import com.pg.merchant.service.MerchantService;
import com.pg.payment.dto.PaymentCreateRequest;
import com.pg.payment.dto.PaymentCreateResponse;
import com.pg.payment.entity.Payment;
import com.pg.payment.entity.PaymentTransaction;
import com.pg.payment.enumtype.PaymentStatus;
import com.pg.payment.enumtype.ResultType;
import com.pg.payment.enumtype.TransactionType;
import com.pg.payment.repository.PaymentRepository;
import com.pg.payment.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCommandService {

    private final PaymentRepository paymentRepository;
    private final MerchantService merchantService;
    private final PaymentTransactionRepository transactionRepository;

    /**
     * 1. 결제 요청 접수 및 원장 생성 (READY)
     */
    public PaymentCreateResponse createPayment(PaymentCreateRequest request) {
        // 가맹점 인증 및 유효성 검사
        Merchant merchant = merchantService.validateMerchant(request.getMerchantUid(), request.getApiKey());

        // 중복 주문 번호 체크 (merchant_id + order_id 유니크 제약 조건 대비)
        if (paymentRepository.existsByMerchantAndOrderId(merchant, request.getOrderId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_ORDER_ID);
        }

        // 고유 TID 생성 (ex: 20260320 + 12자리 랜덤숫자)
        String paymentUid = generatePaymentUid();

        Payment payment = Payment.builder()
                .paymentUid(paymentUid)
                .merchant(merchant)
                .orderId(request.getOrderId())
                .productName(request.getProductName())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.READY)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        return PaymentCreateResponse.of(payment);
    }

    public void updateToSuccess(Payment payment, CardApprovalResponse cardResponse) {
        // 1. 결제 원장 업데이트
        payment.approve(cardResponse.getApprovalCode());

        // 2. 트랜잭션 이력 저장
        // cardResponse.getAmount() 대신 payment.getAmount()를 사용
        saveTransaction(payment, TransactionType.APPROVE, payment.getAmount(),
                ResultType.SUCCESS, cardResponse.getApprovalCode(), null, null);
    }

    public void updateToFail(Payment payment, String errorCode, String errorMessage) {
        // 1. 결제 원장 업데이트 (실패 정보 기록)
         payment.fail(errorCode, errorMessage);

        // 2. 트랜잭션 이력 저장
        saveTransaction(payment, TransactionType.APPROVE, payment.getAmount(),
                ResultType.FAIL, null, errorCode, errorMessage);
    }

    private void saveTransaction(Payment payment, TransactionType transactionType, Long amount,
                                 ResultType result, String appCode, String extCode, String extMsg) {

        // 1. 빌더를 변수에 할당하며 시작 (transaction 변수 선언 확인)
        PaymentTransaction transaction = PaymentTransaction.builder()
                .transactionUid(UUID.randomUUID().toString().replace("-", ""))
                .payment(payment)
                .transactionType(transactionType)
                .amount(amount)
                .resultType(result)
                .approvalCode(appCode)
                .externalCode(extCode)
                .externalMessage(extMsg)
                .requestedAt(LocalDateTime.now())
                .respondedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build(); // 마지막에 세미콜론(;) 확인

        // 2. 레포지토리 저장
        transactionRepository.save(transaction);
    }

    /**
     * 결제 취소 성공 시: 원장 상태 변경 및 트랜잭션 기록
     */
    @Transactional
    public void updateToCancelSuccess(Payment payment, Long cancelAmount, String cancelCode) {
        // 1. 엔티티 내부의 취소 로직 호출 (상태 변경 및 금액 합산)
        payment.cancel(cancelAmount);

        // 2. 취소 트랜잭션 이력 저장 (SUCCESS)
        saveTransaction(
                payment,
                TransactionType.CANCEL,
                cancelAmount,
                ResultType.SUCCESS,
                cancelCode, // 카드사에서 준 cancelCode를 승인번호 칸에 기록
                null,
                null
        );
    }

    /**
     * 결제 취소 실패 시: 트랜잭션 이력만 기록 (원장은 그대로 유지)
     */
    @Transactional
    public void updateToCancelFail(Payment payment, Long cancelAmount, String errorCode, String errorMessage) {
        // 취소 실패는 원장 상태를 바꾸지 않고 이력만 남깁니다.
        saveTransaction(
                payment,
                TransactionType.CANCEL,
                cancelAmount,
                ResultType.FAIL,
                null,
                errorCode,
                errorMessage
        );
    }

    private String generatePaymentUid() {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        StringBuilder randomSuffix = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            randomSuffix.append(random.nextInt(10));
        }
        return datePrefix + randomSuffix.toString();
    }
}