package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.ProductDto;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.*;

/**
 * FAST API - LLM 채팅 서비스 인터페이스
 *
 * v1: Mock 데이터로 구현, 향후 FastAPI 연동 예정
 */
public interface LlmService {
    // 초기 메세지 생성
    BasicChatResponse generateStartMessage(Long customerId, String name);

    // 기분 확인 메세지 생성
    BasicChatResponse generateInputMoodMessage(Long customerId, String name, String mood);

    // 취미 확인 메세지 생성
    BasicChatResponse generateCheckHobbyMessage(Long customerId, String name, String currentHobby);

    // 취미 변경 메세지 생성
    BasicChatResponse generateUpdateHobbyMessage(Long customerId, String name, String newHobby);

    // 최근 구매 카테고리 확인 메세지 생성
    BasicChatResponse generatePurchasesAnalyzeMessage(Long customerId, String name, String category);

    // 추천 메세지 생성
    BasicChatResponse generateRecommendMessage(Long customerId, String name, ProductDto selectedProduct, String mood, String hobby);
}
