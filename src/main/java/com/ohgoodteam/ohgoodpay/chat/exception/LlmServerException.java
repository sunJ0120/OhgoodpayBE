package com.ohgoodteam.ohgoodpay.chat.exception;

public class LlmServerException extends RuntimeException {
    public LlmServerException(String message) {
        super(message);
    }

    public  LlmServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
