package com.merchant.payment.service;

import com.merchant.order.entity.MerchantOrder;
import com.merchant.order.enumtype.OrderStatus;
import com.merchant.order.repository.OrderRepository;
import com.merchant.payment.client.PgClient;
import com.merchant.payment.dto.PaymentRequest;
import com.merchant.payment.dto.PaymentResponse;
import com.merchant.payment.dto.PgPaymentRequest;
import com.merchant.payment.dto.PgPaymentResponse;
import com.merchant.payment.entity.MerchantPayment;
import com.merchant.payment.repository.MerchantPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private static final String MERCHANT_ID = "M001";

    private final OrderRepository orderRepository;
    private final MerchantPaymentRepository merchantPaymentRepository;
    private final PgClient pgClient;

    @Transactional
    public PaymentResponse requestPayment(PaymentRequest request) {
        MerchantOrder order = orderRepository.findByOrderUid(request.getOrderUid())
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다. orderUid=" + request.getOrderUid()));

        MerchantPayment merchantPayment = MerchantPayment.create(order);
        merchantPayment.markRequested();
        merchantPaymentRepository.save(merchantPayment);

        order.changeStatus(OrderStatus.PAYMENT_REQUESTED);

        PgPaymentRequest pgRequest = PgPaymentRequest.builder()
                .merchantUid(MERCHANT_ID)
                .orderId(order.getOrderUid())
                .productName(order.getProductName())
                .amount(order.getAmount())
                .cardNumber(request.getCardNumber())
                .expiryYear(request.getExpiryYear())
                .expiryMonth(request.getExpiryMonth())
                .birthOrBizNo(request.getBirthOrBizNo())
                .cardPassword2Digits(request.getCardPassword2Digits())
                .installmentMonths(request.getInstallmentMonths())
                // 가맹점 정보 및 API Key 추가
                .apiKey("test-api-key-001")
                .paymentMethod("CARD")
                .build();

        // 1. PG사에 결제 생성 요청 (TID 발급)
        PgPaymentResponse createResponse = pgClient.createPayment(pgRequest);
        String tid = createResponse.getPaymentUid();

        // 2. PG사에 결제 승인 요청 (TID 포함)
        PgPaymentResponse pgResponse = pgClient.requestPaymentApproval(pgRequest, tid);

        if ("APPROVED".equalsIgnoreCase(pgResponse.getStatus()) || "SUCCESS".equalsIgnoreCase(pgResponse.getStatus())) {
            merchantPayment.markApproved(
                    pgResponse.getPaymentUid(),
                    pgResponse.getApprovalCode()
            );
            order.changeStatus(OrderStatus.PAYMENT_COMPLETED);
        } else {
            merchantPayment.markFailed(
                    pgResponse.getPaymentUid(),
                    pgResponse.getFailureCode(),
                    pgResponse.getFailureMessage()
            );
            order.changeStatus(OrderStatus.PAYMENT_FAILED);
        }

        return PaymentResponse.from(merchantPayment);
    }
}
