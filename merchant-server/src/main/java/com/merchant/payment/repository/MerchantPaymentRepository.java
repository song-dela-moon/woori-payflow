package com.merchant.payment.repository;

import com.merchant.order.entity.MerchantOrder;
import com.merchant.payment.entity.MerchantPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantPaymentRepository extends JpaRepository<MerchantPayment, Long> {
    Optional<MerchantPayment> findTopByOrderOrderByIdDesc(MerchantOrder order);
}
