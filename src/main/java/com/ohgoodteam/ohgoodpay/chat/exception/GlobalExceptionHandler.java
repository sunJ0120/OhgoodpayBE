package com.ohgoodteam.ohgoodpay.chat.exception;

import com.ohgoodteam.ohgoodpay.recommend.util.ApiResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ApiResponseWrapper<?> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("잘못된 요청 : {}", e.getMessage());
        return ApiResponseWrapper.error(400, e.getMessage());
    }

    @ExceptionHandler(value = LlmServerException.class)
    public ApiResponseWrapper<?> handleLlmServerException(LlmServerException e) {
        log.warn("LLM 서버 오류: {}", e.getMessage());
        return ApiResponseWrapper.error(503, "AI 서비스가 일시적으로 불안정합니다");
    }

    @ExceptionHandler(value = NaverApiException.class)
    public ApiResponseWrapper<?> handleNaverApiException(NaverApiException e) {
        log.warn("네이버 API 오류: {}", e.getMessage());
        return ApiResponseWrapper.error(503, "네이버 상품 검색 서비스가 일시적으로 불안정합니다");
    }

    @ExceptionHandler(value = Exception.class)
    public ApiResponseWrapper<?> handleException(Exception e) {
        log.warn("예상치 못한 오류: {}", e.getMessage());
        return ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다");
    }
}
