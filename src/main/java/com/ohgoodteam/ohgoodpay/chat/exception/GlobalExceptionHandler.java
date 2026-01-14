package com.ohgoodteam.ohgoodpay.chat.exception;

import com.ohgoodteam.ohgoodpay.chat.util.ApiResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ApiResponseWrapper<?>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("잘못된 요청 : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseWrapper.error(400, e.getMessage()));
    }

    @ExceptionHandler(value = NaverApiException.class)
    public ResponseEntity<ApiResponseWrapper<?>> handleNaverApiException(NaverApiException e) {
        log.warn("네이버 API 오류: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponseWrapper.error(503, "네이버 상품 검색 서비스가 일시적으로 불안정합니다"));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponseWrapper<?>> handleException(Exception e) {
        log.warn("예상치 못한 오류: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다"));
    }
}
