package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.util.flow.ChatFlowType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * TODO : 삭제 예정!!!!! 이제 플로우 관리 따로 만들어서 이거 필요 없음~
 * 채팅 플로우 관리 서비스
 *
 * 채팅 플로우의 전환 로직을 담당하며, validation 결과에 따라 다음 플로우 결정
 * Redis 캐싱 없이 기본적인 플로우 관리 로직만 구현
 */
@Service
@Slf4j
public class FlowService {
    /**
     * 플로우 전환 맵 정의
     * 현재 플로우 → 다음 플로우 매핑 (DASHBOARD 제외)
     */
    private static final Map<ChatFlowType, ChatFlowType> FLOW_TRANSITION = Map.of(
            ChatFlowType.START, ChatFlowType.MOODCHECK,
            ChatFlowType.MOODCHECK, ChatFlowType.HOBBYCHECK,
            ChatFlowType.HOBBYCHECK, ChatFlowType.RECOMMENDATION,
            ChatFlowType.RECOMMENDATION, ChatFlowType.RE_RECOMMENDATION
    );

    /**
     * 다음 플로우 결정
     * validation 성공 전제하에 다음 플로우 반환
     *
     * @param currentFlow 현재 플로우
     * @return 다음 플로우
     */
    public String getNextFlow(String currentFlow) {
        try {
            ChatFlowType current = ChatFlowType.fromValue(currentFlow);
            ChatFlowType next = FLOW_TRANSITION.get(current);

            if (next != null) {
                return next.getValue();
            } else {
                return currentFlow;
            }
        } catch (IllegalArgumentException e) {
            return currentFlow;
        }
    }
}