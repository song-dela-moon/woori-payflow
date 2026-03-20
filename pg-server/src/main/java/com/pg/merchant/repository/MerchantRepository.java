package com.pg.merchant.repository;

import com.pg.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    // 가맹점 식별자로 조회
    Optional<Merchant> findByMerchantUid(String mechantUid);

    // API Key 존재 여부 확인
    Optional<Merchant> findByApiKey(String apiKey);
}
