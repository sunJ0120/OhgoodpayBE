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
     * LlmService를 통해 실제 검증 수행
     */
    public ValidInputResponseDTO validateInput(Long customerId, String sessionId, String inputMessage, String flow) {
        ValidInputRequestDTO request = ValidInputRequestDTO.builder()
                .customerId(customerId)
                .sessionId(sessionId)
                .inputMessage(inputMessage)
                .flow(flow)
                .build();

        return llmService.validateInput(request);
    }
}