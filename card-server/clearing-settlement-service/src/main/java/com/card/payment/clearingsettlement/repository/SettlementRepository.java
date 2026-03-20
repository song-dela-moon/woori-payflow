package com.card.payment.clearingsettlement.repository;


import com.card.payment.clearingsettlement.entity.Settlement;
import com.card.payment.clearingsettlement.entity.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    Optional<Settlement> findByMerchantIdAndBusinessDate(String merchantId, LocalDate businessDate);

    List<Settlement> findByStatus(SettlementStatus status);
}