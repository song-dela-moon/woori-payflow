package com.pg.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentApproveRequest {
    @NotBlank(message = "TID는 필수입니다.")
    private String paymentUid;

    @NotBlank(message = "카드 번호는 필수입니다.")
    private String cardNumber;

    @NotBlank(message = "유효기간(년)은 필수입니다.")
    private String expiryYear;

    @NotBlank(message = "유효기간(월)은 필수입니다.")
    private String expiryMonth;

    @NotBlank(message = "생년월일 또는 사업자번호는 필수입니다.")
    private String birthOrBizNo;

    @NotBlank(message = "비밀번호 앞 2자리는 필수입니다.")
    private String cardPassword2Digits;

    private Integer installmentMonths = 0; // 할부개월수 (0: 일시불)
}