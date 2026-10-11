package com.ohgoodteam.ohgoodpay.chat.exception;

import com.ohgoodteam.ohgoodpay.chat.util.ApiResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ApiResponseWrapper<?>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("잘못된 요청 : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseWrapper.error(400, e.getMessage()));
    }

    @ExceptionHandler(value = LlmServerException.class)
    public ResponseEntity<ApiResponseWrapper<?>> handleLlmServerException(LlmServerException e) {
        log.warn("LLM 서버 오류: code={}, message={}", e.getErrorCode(), e.getMessage());
        // ML 이 알려준 errorCode 를 그대로 상위(프론트)로 전달한다. 폴백 정책은 후속 작업 ②
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponseWrapper.error(503, "AI 서비스가 일시적으로 불안정합니다",
                        e.getErrorCode() == null ? null : Map.of("errorCode", e.getErrorCode())));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponseWrapper<?>> handleException(Exception e) {
        log.warn("예상치 못한 오류: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다"));
    }
}
