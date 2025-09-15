package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.ValidInputRequestDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.ValidInputResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.service.fastapi.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 사용자 입력 검증 서비스
 *
 * ChatService의 validation 로직을 분리하여 책임을 명확히 함
 * LlmService를 통해 FastAPI와 통신하여 입력 유효성 검증
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ValidCheckService {
    private final LlmService llmService;

    /**
     * 사용자 입력이 현재 플로우에 유효한지 검증
     */
    public boolean isValidInput(Long customerId, String sessionId, String inputMessage, String flow) {
        ValidInputResponseDTO result = validateInput(customerId, sessionId, inputMessage, flow);
        return result.isValid();
    }

    /**
     * 검증 실패시 에러 메시지 반환
     */
    public String getValidationMessage(Long customerId, String sessionId, String inputMessage, String flow) {
        ValidInputResponseDTO result = validateInput(customerId, sessionId, inputMessage, flow);
        return result.getMessage();
    }

    /**
     * LlmService를 통해 실제 검증 수행
     */
    private ValidInputResponseDTO validateInput(Long customerId, String sessionId, String inputMessage, String flow) {
        ValidInputRequestDTO request = ValidInputRequestDTO.builder()
                .customerId(customerId)
                .sessionId(sessionId)
                .inputMessage(inputMessage)
                .flow(flow)
                .build();

        return llmService.validateInput(request);
    }
}