package com.ohgoodteam.ohgoodpay.recommend.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponseWrapper<T> {
    private boolean success;
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponseWrapper<T> ok(T data) {
        return ApiResponseWrapper.<T>builder()
                .success(true)
                .code(200)
                .message("OK")
                .data(data)
                .build();
    }

    public static <T> ApiResponseWrapper<T> error(int code, String message) {
        return ApiResponseWrapper.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }
}
