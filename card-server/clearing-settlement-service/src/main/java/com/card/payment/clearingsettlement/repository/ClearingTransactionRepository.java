package com.card.payment.clearingsettlement.repository;


import com.card.payment.clearingsettlement.entity.ClearingStatus;
import com.card.payment.clearingsettlement.entity.ClearingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClearingTransactionRepository extends JpaRepository<ClearingTransaction, Long> {

    boolean existsByEventId(String eventId);

    boolean existsByTransactionId(String transactionId);

    List<ClearingTransaction> findByStatus(ClearingStatus status);

    List<ClearingTransaction> findByMerchantIdAndStatus(String merchantId, ClearingStatus status);

    Optional<ClearingTransaction> findByTransactionId(String transactionId);
}