package com.pg.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCancelRequest {

    @NotBlank(message = "TID(paymentUid)는 필수입니다.")
    private String paymentUid;

    @NotBlank(message = "가맹점 식별자(merchantUid)는 필수입니다.")
    private String merchantUid;

    @NotNull(message = "취소 금액은 필수입니다.")
    @Min(value = 1, message = "취소 금액은 1원 이상이어야 합니다.")
    private Long cancelAmount;

    @NotBlank(message = "취소 사유는 필수입니다.")
    private String reason;

    @NotBlank(message = "API Key는 필수입니다.")
    private String apiKey;
}