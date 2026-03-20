package com.card.payment.clearingsettlement.repository;


import com.card.payment.clearingsettlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    Optional<Settlement> findByMerchantIdAndBusinessDate(String merchantId, LocalDate businessDate);
}