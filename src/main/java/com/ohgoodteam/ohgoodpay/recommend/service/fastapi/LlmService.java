package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.*;

/**
 * FAST API - LLM 채팅 서비스 인터페이스
 *
 * v1: Mock 데이터로 구현, 향후 FastAPI 연동 예정
 */
public interface LlmService {
    // 채팅 생성
    BasicChatResponseDTO generateChat(
            String sessionId,
            CustomerCacheDTO customerInfo,
            String mood,
            String hobby,
            int balance,
            String inputMessage,
            String summary,
            String flow
    );
}
