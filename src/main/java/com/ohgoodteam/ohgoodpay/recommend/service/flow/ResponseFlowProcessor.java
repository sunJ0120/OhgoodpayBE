package com.ohgoodteam.ohgoodpay.recommend.service.flow;

import com.ohgoodteam.ohgoodpay.recommend.dto.FlowContext;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.BasicChatResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.util.flow.FlowType;

public class ResponseFlowProcessor implements FlowProcessor{
    @Override
    public BasicChatResponseDTO process(FlowContext context) {
        // 1. 현재 플로우에 대한 입력 검증

        // 2. 검증 실패 처리

        // 3. 검증 성공시 입력값 캐싱 (products)

        // 4. 현재 플로우로 LLM 생성 요청
        return null;
    }

    @Override
    public boolean canHandle(FlowType flowType) {
        return false;
    }

    // 질문 카운트 두 번 이상일 때 처리할 handler 메서드
}