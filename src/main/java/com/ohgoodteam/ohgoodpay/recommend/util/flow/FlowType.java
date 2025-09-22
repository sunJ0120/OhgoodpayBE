package com.ohgoodteam.ohgoodpay.recommend.util.flow;

/**
 * ChatFlowType을 큰 틀에서 관리하기 위한 ENUM 클래스
 */
public enum FlowType {
    START, // 시작 플로우 (검증 없음)
    QUESTION,     // 다음 질문을 생성하는 플로우
    REGENERATE    // 재생성, LLM 연동 없음 일단은....
}
