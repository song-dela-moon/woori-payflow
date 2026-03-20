package com.pg.payment.repository;

import com.pg.merchant.entity.Merchant;
import com.pg.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentUid(String paymentUid);
    boolean existsByMerchantAndOrderId(Merchant merchant, String orderId);
}