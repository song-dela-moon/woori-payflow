package com.merchant.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PgApiResponse<T> {
    private String message;
    private int status;
    private T data;
}
