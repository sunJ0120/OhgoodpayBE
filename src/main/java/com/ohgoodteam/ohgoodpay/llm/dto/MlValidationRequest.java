package com.ohgoodteam.ohgoodpay.llm.dto;

/** POST /ml/chat/validation 요청. */
public record MlValidationRequest(
        Long customerId,
        String sessionId,
        String inputMessage,
        String flow,
        MlPrompts prompts
) {
}
