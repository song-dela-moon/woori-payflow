package com.card.payment.clearingsettlement.repository;

import com.card.payment.clearingsettlement.entity.MerchantAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantAccountRepository extends JpaRepository<MerchantAccount, Long> {
    Optional<MerchantAccount> findByMerchantId(String merchantId);
}