package com.ohgoodteam.ohgoodpay.llm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** LLM 호출 1건의 메타데이터. ML 이 만들고 BE 가 llm_call_log 로 저장한다. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MlCallMeta(
        String slot,
        String promptKey,
        int promptVersion,
        String model,
        int latencyMs,
        int tokensIn,
        int tokensOut,
        boolean valid,
        String errorCode
) {
}
