package com.pg.merchant.service;

import com.pg.common.exception.BusinessException;
import com.pg.common.exception.ErrorCode;
import com.pg.merchant.entity.Merchant;
import com.pg.merchant.repository.MerchantRepository;
import com.pg.payment.enumtype.MerchantStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MerchantService {

    private final MerchantRepository merchantRepository;

    /**
     * 가맹점 인증 및 상태 검증
     */
    public Merchant validateMerchant(String merchantUid, String apiKey) {
        log.info("Validating merchant with uid: {}, apiKey: {}", merchantUid, apiKey);
        // 가맹점 존재 확인
        Merchant merchant = merchantRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> {
                    log.error("Merchant NOT FOUND for uid: {}", merchantUid);
                    return new BusinessException(ErrorCode.MERCHANT_NOT_FOUND);
                });

        // API Key 일치 확인
        if (!merchant.getApiKey().equals(apiKey)) {
            throw new BusinessException(ErrorCode.INVALID_API_KEY);
        }

        // 가맹점 활성화 상태 확인
        if (merchant.getStatus() != MerchantStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.MERCHANT_INACTIVE);
        }

        return merchant;
    }
}