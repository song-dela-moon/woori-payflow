package com.merchant.payment.service;

import com.merchant.order.entity.MerchantOrder;
import com.merchant.order.enumtype.OrderStatus;
import com.merchant.order.repository.OrderRepository;
import com.merchant.payment.client.PgClient;
import com.merchant.payment.dto.PaymentRequest;
import com.merchant.payment.dto.PaymentResponse;
import com.merchant.payment.dto.PgApprovePaymentRequest;
import com.merchant.payment.dto.PgApprovePaymentResponse;
import com.merchant.payment.dto.PgCreatePaymentRequest;
import com.merchant.payment.dto.PgCreatePaymentResponse;
import com.merchant.payment.dto.PgWebhookRequest;
import com.merchant.payment.entity.MerchantPayment;
import com.merchant.payment.repository.MerchantPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private static final String MERCHANT_UID = "MRC-001";
    private static final String API_KEY = "test-api-key-001";
    private static final String PAYMENT_METHOD = "CARD";

    private final OrderRepository orderRepository;
    private final MerchantPaymentRepository merchantPaymentRepository;
    private final PgClient pgClient;

    @Transactional
    public PaymentResponse requestPayment(PaymentRequest request) {
        MerchantOrder order = orderRepository.findByOrderUid(request.getOrderUid())
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다. orderUid=" + request.getOrderUid()));

        MerchantPayment merchantPayment = MerchantPayment.create(order);
        merchantPaymentRepository.save(merchantPayment);

        order.changeStatus(OrderStatus.PAYMENT_REQUESTED);

        PgCreatePaymentRequest createRequest = PgCreatePaymentRequest.builder()
                .merchantUid(MERCHANT_UID)
                .orderId(order.getOrderUid())
                .productName(order.getProductName())
                .amount(order.getAmount())
                .paymentMethod(PAYMENT_METHOD)
                .apiKey(API_KEY)
                .build();

        PgCreatePaymentResponse createResponse = pgClient.createPayment(createRequest);
        merchantPayment.markRequested(createResponse.getPaymentUid());

        PgApprovePaymentRequest approveRequest = PgApprovePaymentRequest.builder()
                .paymentUid(createResponse.getPaymentUid())
                .cardNumber(request.getCardNumber())
                .expiryYear(request.getExpiryYear())
                .expiryMonth(request.getExpiryMonth())
                .birthOrBizNo(request.getBirthOrBizNo())
                .cardPassword2Digits(request.getCardPassword2Digits())
                .installmentMonths(request.getInstallmentMonths())
                .build();

        PgApprovePaymentResponse approveResponse = pgClient.approvePayment(approveRequest);
        applyPgResult(order, merchantPayment, approveResponse);

        return PaymentResponse.from(merchantPayment);
    }

    @Transactional
    public void handlePaymentWebhook(PgWebhookRequest request) {
        MerchantOrder order = orderRepository.findByOrderUid(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다. orderId=" + request.getOrderId()));

        MerchantPayment merchantPayment = merchantPaymentRepository.findTopByOrderOrderByIdDesc(order)
                .orElseThrow(() -> new IllegalArgumentException("결제 이력이 존재하지 않습니다. orderId=" + request.getOrderId()));

        if ("APPROVED".equalsIgnoreCase(request.getStatus())) {
            merchantPayment.markApproved(request.getPaymentId(), request.getApprovalCode());
            order.changeStatus(OrderStatus.PAYMENT_COMPLETED);
            return;
        }

        if ("FAILED".equalsIgnoreCase(request.getStatus())) {
            merchantPayment.markFailed(request.getPaymentId(), request.getFailureCode(), request.getFailureMessage());
            order.changeStatus(OrderStatus.PAYMENT_FAILED);
            return;
        }

        if ("CANCELLED".equalsIgnoreCase(request.getStatus())) {
            merchantPayment.markCanceled(request.getPaymentId());
            order.changeStatus(OrderStatus.CANCELLED);
            return;
        }

        throw new IllegalArgumentException("지원하지 않는 PG 상태입니다. status=" + request.getStatus());
    }

    private void applyPgResult(
            MerchantOrder order,
            MerchantPayment merchantPayment,
            PgApprovePaymentResponse approveResponse
    ) {
        String status = approveResponse.getStatus();

        if ("SUCCESS".equalsIgnoreCase(status)) {
            merchantPayment.markApproved(approveResponse.getPaymentUid(), approveResponse.getApprovalCode());
            order.changeStatus(OrderStatus.PAYMENT_COMPLETED);
            return;
        }

        if ("FAIL".equalsIgnoreCase(status)) {
            merchantPayment.markFailed(
                    approveResponse.getPaymentUid(),
                    approveResponse.getFailureCode(),
                    approveResponse.getFailureMessage()
            );
            order.changeStatus(OrderStatus.PAYMENT_FAILED);
            return;
        }

        if ("CANCELED".equalsIgnoreCase(status) || "PARTIAL_CANCELED".equalsIgnoreCase(status)) {
            merchantPayment.markCanceled(approveResponse.getPaymentUid());
            order.changeStatus(OrderStatus.CANCELLED);
            return;
        }

        if ("READY".equalsIgnoreCase(status) || "PENDING".equalsIgnoreCase(status)) {
            merchantPayment.markRequested(approveResponse.getPaymentUid());
            order.changeStatus(OrderStatus.PAYMENT_REQUESTED);
            return;
        }

        throw new IllegalArgumentException("지원하지 않는 PG 상태입니다. status=" + status);
    }
}
