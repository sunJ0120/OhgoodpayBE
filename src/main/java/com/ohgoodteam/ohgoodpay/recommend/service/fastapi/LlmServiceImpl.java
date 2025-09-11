package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.ConsumerContextDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.ProductDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.RecommendMessageRequestDTO;
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

    // llm으로 초기 메세지 생성
    @Override
    public BasicChatResponseDTO generateStartMessage(Long customerId, String name) {
        // FAST API 요청 DTO 생성
        StartChatRequestDTO request = StartChatRequestDTO.of(customerId, name);
        
        return fastApiClient.post("/chat/greeting", request, BasicChatResponseDTO.class);
    }

    // llm으로 기분 확인 메세지 생성
    @Override
    public BasicChatResponseDTO generateInputMoodMessage(Long customerId, String name, String mood) {
        // FAST API 요청 DTO 생성
        InputMoodRequestDTO request = InputMoodRequestDTO.of(customerId, name, mood);

        return fastApiClient.post("/chat/mood-response", request, BasicChatResponseDTO.class);
    }

    // llm으로 취미 확인 메세지 생성
    @Override
    public BasicChatResponseDTO generateCheckHobbyMessage(Long customerId, String name, String currentHobby) {
        // FAST API 요청 DTO 생성
        CheckHobbyRequestDTO request = CheckHobbyRequestDTO.of(customerId, name, currentHobby);

        return fastApiClient.post("/chat/hobby-check", request, BasicChatResponseDTO.class);
    }

    // llm으로 취미 변경 메세지 생성
    @Override
    public BasicChatResponseDTO generateUpdateHobbyMessage(Long customerId, String name, String newHobby) {
        UpdateHobbyRequestDTO request = UpdateHobbyRequestDTO.of(customerId, name, newHobby);

        return fastApiClient.post("/chat/hobby-update", request, BasicChatResponseDTO.class);
    }

    // llm으로 최근 구매 카테고리 확인 메세지 생성
    @Override
    public BasicChatResponseDTO generatePurchasesAnalyzeMessage(Long customerId, String name, String category) {
        PurchasesAnalyzeRequestDTO request = PurchasesAnalyzeRequestDTO.of(customerId, name, category);

        return fastApiClient.post("/chat/analyze-purchases", request, BasicChatResponseDTO.class);
    }

    // llm으로 추천 메세지 생성
    @Override
    public BasicChatResponseDTO generateRecommendMessage(Long customerId, String name, ProductDTO selectedProduct, String mood, String hobby) {
        RecommendMessageRequestDTO request = RecommendMessageRequestDTO.of(customerId, name, selectedProduct, ConsumerContextDTO.of(mood, hobby));

        return fastApiClient.post("/chat/recommend-message", request, BasicChatResponseDTO.class);
    }
}
