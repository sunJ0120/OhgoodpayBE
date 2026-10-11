package com.ohgoodteam.ohgoodpay.llm.dto;

import com.fasterxml.jackson.databind.JsonNode;

/** ML 요청에 싣는 프롬프트 1건 (prompt_version 행). content 는 {{변수}} 템플릿이다. */
public record PromptSpecDto(
        String promptKey,
        int promptVersion,
        String content,
        JsonNode responseSchema,
        String model
) {
}
