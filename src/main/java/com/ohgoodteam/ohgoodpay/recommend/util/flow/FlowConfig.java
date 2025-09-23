package com.ohgoodteam.ohgoodpay.recommend.util.flow;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 현재 플로우, 다음 플로우를 설정하는 역할을 한다.
 */
@RequiredArgsConstructor
@Getter
public class FlowConfig {
    // 지금 마지막 플로우인지 & 최대 재요청 카운트 이거 두 개는 굳이 고려할 필요가 없는거 같아서 일단 주석 처리
    private final FlowType processingType;     // 처리 방식 (QUESTION/RESPONSE)
    private final ChatFlowType nextFlow;      // 다음 플로우
//    private final boolean isTerminal;         // 마지막 플로우인지
//    private final int maxRetryCnt; // 최대 재요청 카운트
}
