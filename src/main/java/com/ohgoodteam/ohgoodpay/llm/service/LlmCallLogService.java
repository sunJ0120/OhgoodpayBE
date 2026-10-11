package com.ohgoodteam.ohgoodpay.llm.service;

import com.ohgoodteam.ohgoodpay.llm.dto.MlCallMeta;
import com.ohgoodteam.ohgoodpay.llm.entity.LlmCallLogEntity;
import com.ohgoodteam.ohgoodpay.llm.repository.LlmCallLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * ML 응답의 호출 메타데이터를 llm_call_log 로 저장한다.
 * 받는 값은 (대화 id, 단계, 메타데이터)뿐이라 사용자 원문이 들어올 자리가 없다.
 * 기록 실패가 대화를 막으면 안 되므로 예외는 삼키고 로그만 남긴다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LlmCallLogService {
    private final LlmCallLogRepository repository;

    public void record(String conversationId, String flowStep, List<MlCallMeta> calls) {
        if (calls == null || calls.isEmpty()) {
            return;
        }
        String requestId = UUID.randomUUID().toString();
        try {
            repository.saveAll(calls.stream().map(c -> LlmCallLogEntity.builder()
                    .conversationId(conversationId)
                    .flowStep(flowStep)
                    .slot(c.slot())
                    .requestId(requestId)
                    .promptKey(c.promptKey())
                    .promptVersion(c.promptVersion())
                    .model(c.model())
                    .latencyMs(c.latencyMs())
                    .tokensIn(c.tokensIn())
                    .tokensOut(c.tokensOut())
                    .valid(c.valid())
                    .errorCode(c.errorCode())
                    .build()).toList());
        } catch (Exception e) {
            log.error("LLM 호출 기록 저장 실패: conversation={}, step={}, error={}", conversationId, flowStep, e.toString());
        }
    }
}
