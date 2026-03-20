package com.pg.payment.service;

import com.pg.common.exception.BusinessException;
import com.pg.common.exception.ErrorCode;
import com.pg.payment.entity.Payment;
import com.pg.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryService {

    private final PaymentRepository paymentRepository;

    public Payment getPaymentByUid(String paymentUid) {
        return paymentRepository.findByPaymentUid(paymentUid)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }
}