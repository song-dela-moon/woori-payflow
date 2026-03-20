package com.card.payment.clearingsettlement.repository;

import com.card.payment.clearingsettlement.entity.Payout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutRepository extends JpaRepository<Payout, Long> {
    boolean existsBySettlementId(Long settlementId);
}
