package com.ohgoodteam.ohgoodpay.llm.dto;

/** main 은 필수, summary(요약 갱신)는 선택. */
public record MlPrompts(PromptSpecDto main, PromptSpecDto summary) {
}
