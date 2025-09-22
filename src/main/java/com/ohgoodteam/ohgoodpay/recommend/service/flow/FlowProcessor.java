package com.ohgoodteam.ohgoodpay.recommend.service.flow;

import com.ohgoodteam.ohgoodpay.recommend.dto.FlowContext;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.BasicChatResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.util.flow.FlowType;

/**
 * 각 플로우 별로 나눠서 채팅을 관리 하기 위한
 */
public interface FlowProcessor {
    BasicChatResponseDTO process(FlowContext context);
    boolean canHandle(FlowType flowType);
}
