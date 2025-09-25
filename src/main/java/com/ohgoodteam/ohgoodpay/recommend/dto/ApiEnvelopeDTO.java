package com.ohgoodteam.ohgoodpay.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder @AllArgsConstructor
public class ApiEnvelopeDTO<T> {
    private boolean success;  // true/false
    private String code;      // "200" 등 문자열
    private String message;   // "success" 등
    private T data;           // 페이로드

    /* 출력 형태 고정 */
    public static <T> ApiEnvelopeDTO<T> ok(T data) {
        return ApiEnvelopeDTO.<T>builder().success(true).code("200").message("success").data(data).build();
    }
}