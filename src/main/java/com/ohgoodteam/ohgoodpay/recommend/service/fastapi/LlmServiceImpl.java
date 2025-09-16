package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDTO;

import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.*;
import com.ohgoodteam.ohgoodpay.recommend.util.FastApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * FAST API - LLM 채팅 서비스 구현체
 *
 * v1: Mock 데이터로 구현, 향후 FastAPI 연동 예정
 */
@Service
@RequiredArgsConstructor
public class LlmServiceImpl implements LlmService {
    private final FastApiClient fastApiClient;

    @Override
    public BasicChatResponseDTO generateChat(
            String sessionId,
            CustomerCacheDTO customerInfo,
            String mood,
            String hobby,
            int balance,
            String inputMessage,
            String summary,
            String flow
    ) {
        // FAST API 요청 DTO 생성
        // 여기 안쪽에서 자세한 DTO를 생성하도록 한다.
        BasicChatRequestDTO request = BasicChatRequestDTO.of(
                sessionId,
                customerInfo,
                mood,
                hobby,
                balance,
                inputMessage,
                summary,
                flow
        );
        return fastApiClient.post("/chat", request, BasicChatResponseDTO.class);
    }

    //TODO : FAST API 연동 필요
    @Override
    public ValidInputResponseDTO validateInput(ValidInputRequestDTO request) {
        return fastApiClient.post("/validation", request, ValidInputResponseDTO.class);
    }
}
