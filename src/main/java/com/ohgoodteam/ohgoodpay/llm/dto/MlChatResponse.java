package com.ohgoodteam.ohgoodpay.llm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/** POST /ml/chat 응답. main 호출이 실패하면 message 가 null 이고 calls 에 errorCode 가 있다. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MlChatResponse(
        String sessionId,
        String message,
        String newHobby,
        List<MlProduct> products,
        String summary,
        String flow,
        String keyword,
        JsonNode result,
        List<MlCallMeta> calls
) {
}
