package com.pg.payment.controller;

import com.pg.common.response.ApiResponse;
import com.pg.payment.dto.*;
import com.pg.payment.service.PaymentService;
import com.pg.payment.service.PaymentCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentCommandService paymentCommandService;
    private final PaymentService paymentService;

    /**
     * 1. 결제 생성 (TID 발행)
     */
    @PostMapping("/create")
    public ApiResponse<PaymentCreateResponse> createPayment(
            @RequestBody @Valid PaymentCreateRequest request) {
        return ApiResponse.success("결제 준비 완료", paymentCommandService.createPayment(request));
    }

    /**
     * 2. 결제 승인 (카드사 연동)
     */
    @PostMapping("/approve")
    public ApiResponse<PaymentResponse> approve(@RequestBody @Valid PaymentApproveRequest req) {
        // 님의 PaymentService가 내부적으로 카드사 통신 -> DB저장 -> 웹훅까지 다 하고
        // 최종 결과인 PaymentResponse를 리턴해줍니다.
        PaymentResponse response = paymentService.approvePayment(req);

        // 이 response가 JSON으로 변환되어 결제창(Client)으로 날아갑니다.
        return ApiResponse.success("결제 승인 완료", response);
    }

    /**
     * 결제 취소 (전액/부분)
     */
    @PostMapping("/cancel")
    public ApiResponse<PaymentResponse> cancelPayment(@RequestBody @Valid PaymentCancelRequest request) {
        PaymentResponse response = paymentService.cancelPayment(request);
        return ApiResponse.success("취소 처리가 완료되었습니다.", response);
    }
}