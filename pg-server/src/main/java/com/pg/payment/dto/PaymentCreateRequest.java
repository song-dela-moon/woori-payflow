package com.pg.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCreateRequest {

    @NotBlank(message = "가맹점 식별자는 필수입니다.")
    private String merchantUid;

    @NotBlank(message = "주문 번호는 필수입니다.")
    private String orderId;

    @NotBlank(message = "상품명은 필수입니다.")
    private String productName;

    @NotNull(message = "결제 금액은 필수입니다.")
    @Min(value = 100, message = "최소 결제 금액은 100원입니다.")
    private Long amount;

    @NotBlank(message = "결제 수단은 필수입니다.")
    private String paymentMethod;

    @NotBlank(message = "API Key는 필수입니다.")
    private String apiKey;
}
