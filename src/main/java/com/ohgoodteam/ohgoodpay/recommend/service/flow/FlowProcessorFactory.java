package com.ohgoodteam.ohgoodpay.recommend.service.flow;

import com.ohgoodteam.ohgoodpay.recommend.util.flow.FlowType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 플로우 관리 및 service에서 플로우 관련 프로세서를 가져오게 하기 위한 팩터리 메서드
 */
@Component
@RequiredArgsConstructor
public class FlowProcessorFactory {

    private final List<FlowProcessor> processors;

    public FlowProcessor getProcessor(FlowType flowType) {
        return processors.stream()
                .filter(processor -> processor.canHandle(flowType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("processor 타입을 찾을 수 없습니다. : " + flowType));
    }
}
