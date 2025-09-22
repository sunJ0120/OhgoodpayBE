package com.ohgoodteam.ohgoodpay.recommend.service.flow;

import com.ohgoodteam.ohgoodpay.recommend.dto.FlowContext;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.BasicChatResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.util.flow.FlowType;

public class StartFlowProcessor implements FlowProcessor{
    @Override
    public BasicChatResponseDTO process(FlowContext context) {
        // 1. 다음 플로우로 전환

        // 2. 다음 플로우에 대한 질문 생성
        return null;
    }

    @Override
    public boolean canHandle(FlowType flowType) {
        return false;
    }
}
