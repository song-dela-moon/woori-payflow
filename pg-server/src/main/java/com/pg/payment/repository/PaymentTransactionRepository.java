package com.pg.payment.repository;

import com.pg.payment.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    // 특정 결제건에 대한 모든 트랜잭션 이력 조회 (나중에 정산이나 CS용으로 쓰임)
    List<PaymentTransaction> findByPaymentId(Long paymentId);

    // TID로 이력 조회
    List<PaymentTransaction> findByPaymentPaymentUid(String paymentUid);
}