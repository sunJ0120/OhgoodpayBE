package com.ohgoodteam.ohgoodpay.llm.dto;

/** POST /ml/chat 요청. 필드 이름은 ML(FastAPI)의 BasicChatRequest 별칭(camelCase)과 같다. */
public record MlChatRequest(
        String sessionId,
        String flow,
        String inputMessage,
        MlCustomerInfo customerInfo,
        String mood,
        String hobby,
        int balance,
        String summary,
        String prevKeyword,
        MlPrompts prompts
) {
}
