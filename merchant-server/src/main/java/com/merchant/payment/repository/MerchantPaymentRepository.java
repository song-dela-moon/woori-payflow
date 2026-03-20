package com.merchant.payment.repository;

import com.merchant.payment.entity.MerchantPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantPaymentRepository extends JpaRepository<MerchantPayment, Long> {
}
