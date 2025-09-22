package com.ohgoodteam.ohgoodpay.recommend.service.flow;

import com.ohgoodteam.ohgoodpay.recommend.dto.FlowContext;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.BasicChatResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.util.flow.FlowType;

/**
 * 각 플로우 별로 나눠서 채팅을 관리 하기 위한
 */
public interface FlowProcessor {
    // 프로세서별 프로세스 정의
    BasicChatResponseDTO process(FlowContext context);
    // 프로세서 팩토리 메서드에서 가능한 프로세서를 뽑기 위함.
    boolean canHandle(FlowType flowType);
}
