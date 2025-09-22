package com.ohgoodteam.ohgoodpay.recommend.util.flow;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * FlowConfig를 이용해서 다음 처리 방식을 결정한다.
 *
 * next flow에 대한 것 명시 되어 있음.
 * 일단 임시로 RE_RECOMMENDATION 다음 플로우를 RE_RECOMMENDATION로 잡는다.
 */
@Component
public class FlowConfiguration {
    private final Map<ChatFlowType, FlowConfig> flowConfigs = Map.of(
            ChatFlowType.START, new FlowConfig(FlowType.START, ChatFlowType.MOODCHECK),
            ChatFlowType.MOODCHECK, new FlowConfig(FlowType.QUESTION, ChatFlowType.HOBBYCHECK),
            ChatFlowType.HOBBYCHECK, new FlowConfig(FlowType.QUESTION, ChatFlowType.CHOOSE),
            ChatFlowType.CHOOSE, new FlowConfig(FlowType.QUESTION, ChatFlowType.RECOMMENDATION),
            ChatFlowType.RECOMMENDATION, new FlowConfig(FlowType.QUESTION, ChatFlowType.RE_RECOMMENDATION),
            ChatFlowType.RE_RECOMMENDATION, new FlowConfig(FlowType.QUESTION, ChatFlowType.RE_RECOMMENDATION)
    );

    public FlowConfig getFlowConfig(ChatFlowType currentFlow) {
        return flowConfigs.get(currentFlow);
    }
}
