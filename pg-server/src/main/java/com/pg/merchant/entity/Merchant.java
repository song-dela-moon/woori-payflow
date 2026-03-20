package com.pg.merchant.entity;

import com.pg.common.exception.BusinessException;
import com.pg.common.exception.ErrorCode;
import com.pg.payment.enumtype.MerchantStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_uid", unique = true, nullable = false)
    private String merchantUid;

    private String name;

    @Column(name = "api_key", unique = true, nullable = false)
    private String apiKey;

    private String callbackUrl;

    @Enumerated(EnumType.STRING)
    private MerchantStatus status; // ACTIVE, INACTIVE

    @CreatedDate // 데이터 생성 시 자동으로 현재 시간 주입
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // 수정 시 자동으로 현재 시간 주입
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 가맹점 검증 로직
    public void validate() {
        if (this.status != MerchantStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.MERCHANT_INACTIVE);
        }
    }
}
