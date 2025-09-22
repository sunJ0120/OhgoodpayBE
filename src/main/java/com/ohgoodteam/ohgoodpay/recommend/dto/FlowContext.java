package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.util.flow.ChatFlowType;
import lombok.Builder;
import lombok.Getter;

/**
 * boot서버 내부에서 flow 검증을 위한 내부 DTO
 */
@Builder
@Getter
public class FlowContext {
    private Long customerId;
    private String sessionId;
    private String message; //해당 메세지의 플로우 검증을 위함
    private ChatFlowType currentFlow; //현재 플로우의 플로우 검증을 위함
}
