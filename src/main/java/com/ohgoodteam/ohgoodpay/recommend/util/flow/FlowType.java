package com.ohgoodteam.ohgoodpay.recommend.util.flow;

/**
 * ChatFlowType을 큰 틀에서 관리하기 위한 ENUM 클래스
 */
public enum FlowType {
    QUESTION,     // 다음 질문을 생성하는 플로우
    RESPONSE,     // 현재 입력에 대한 응답을 생성하는 플로우
    TRANSITION    // 단순 전환만 하는 플로우
}
