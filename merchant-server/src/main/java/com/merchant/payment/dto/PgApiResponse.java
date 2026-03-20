package com.merchant.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PgApiResponse<T> {

    private String message;
    private int status;
    private T data;
}
