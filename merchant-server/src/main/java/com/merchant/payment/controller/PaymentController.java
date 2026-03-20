package com.merchant.payment.controller;

import com.merchant.payment.dto.PaymentRequest;
import com.merchant.payment.dto.PaymentResponse;
import com.merchant.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse requestPayment(@RequestBody PaymentRequest request) {
        return paymentService.requestPayment(request);
    }
}
