package com.ohgoodteam.ohgoodpay.llm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** POST /ml/chat/validation 응답. LLM 호출 자체가 실패하면 isValid=false 이고 calls[0] 에 errorCode 가 있다. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MlValidationResponse(
        Long customerId,
        String sessionId,
        String inputMessage,
        @JsonProperty("isValid") boolean isValid,
        String message,
        String normalized,
        String flow,
        List<MlCallMeta> calls
) {
}
