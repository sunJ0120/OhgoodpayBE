package com.ohgoodteam.ohgoodpay.chat.exception;

import lombok.Getter;

/**
 * LLM(ML 서버) 호출 실패. errorCode 는 ML 이 알려준 값을 그대로 담는다:
 * TIMEOUT, SCHEMA_INVALID, LLM_ERROR, TEMPLATE_ERROR, 그리고 BE 가 ML 에 닿지 못했을 때의 ML_ERROR.
 */
@Getter
public class LlmServerException extends RuntimeException {
    private final String errorCode;

    public LlmServerException(String message) {
        this(message, (String) null);
    }

    public LlmServerException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public LlmServerException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public LlmServerException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }
}
