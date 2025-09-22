package com.ohgoodteam.ohgoodpay.recommend.util.flow;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * FlowConfig를 이용해서 다음 처리 방식을 결정한다.
 *
 * next flow에 대한 것 명시 되어 있음.
 */
@Component
public class FlowConfiguration {
    private final Map<ChatFlowType, FlowConfig> flowConfigs = Map.of(
            ChatFlowType.MOODCHECK, new FlowConfig(FlowType.QUESTION, ChatFlowType.HOBBYCHECK),
            ChatFlowType.HOBBYCHECK, new FlowConfig(FlowType.QUESTION, ChatFlowType.RECOMMENDATION),
            ChatFlowType.RECOMMENDATION, new FlowConfig(FlowType.RESPONSE, ChatFlowType.RE_RECOMMENDATION),
            ChatFlowType.RE_RECOMMENDATION, new FlowConfig(FlowType.RESPONSE, null)
    );

    public FlowConfig getFlowConfig(ChatFlowType currentFlow) {
        return flowConfigs.get(currentFlow);
    }
}
