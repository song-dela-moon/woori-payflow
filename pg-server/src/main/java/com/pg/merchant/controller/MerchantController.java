package com.pg.merchant.controller;

import com.pg.common.response.ApiResponse;
import com.pg.merchant.entity.Merchant;
import com.pg.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantRepository merchantRepository;

    /**
     * 테스트용 가맹점 등록 API
     */
    @PostMapping("/register")
    public ApiResponse<Merchant> registerMerchant(@RequestBody Merchant merchant) {
        // 실제 운영 환경에서는 별도의 DTO를 써야 하지만,
        // 테스트용이므로 엔티티를 직접 받아 저장합니다.
        Merchant savedMerchant = merchantRepository.save(merchant);
        return ApiResponse.success("가맹점 등록 성공", savedMerchant);
    }

    /**
     * 전체 가맹점 목록 조회 (등록된 정보를 확인하기 위함)
     */
    @GetMapping
    public ApiResponse<List<Merchant>> getAllMerchants() {
        return ApiResponse.success("가맹점 목록 조회 성공", merchantRepository.findAll());
    }
}