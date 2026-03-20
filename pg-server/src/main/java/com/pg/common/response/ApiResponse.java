package com.pg.common.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiResponse<T> {

    private String message;
    private int status;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("성공", 200, data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, 200, data);
    }
}