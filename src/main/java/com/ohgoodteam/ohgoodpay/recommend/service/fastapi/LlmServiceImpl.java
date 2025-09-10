package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.ConsumerContextDto;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.ProductDto;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.RecommendMessageRequest;
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
    public BasicChatResponse generateStartMessage(Long customerId, String name) {
        // FAST API 요청 DTO 생성
        StartChatRequest request = StartChatRequest.of(customerId, name);
        
//        return fastApiClient.post("/greeting", request, BasicChatResponse.class);
        return BasicChatResponse.builder()
                .message(String.format("안녕 나는 너만의 오레이봉봉 ~ 나를 레이라고 불러줘 %s~ 오늘 기분은 어때?", name))
                .build();
    }

    // llm으로 기분 확인 메세지 생성
    @Override
    public BasicChatResponse generateInputMoodMessage(Long customerId, String name, String mood) {
        // FAST API 요청 DTO 생성
        InputMoodRequest request = InputMoodRequest.of(customerId, name, mood);

//        return fastApiClient.post("/mood-response", request, BasicChatResponse.class);
        return BasicChatResponse.builder()
                .message(String.format("%s이가 기분이 %s하다니 나도 좋은걸~ 그럼 오늘 뭐가 필요한지 알아볼까?", request.getName(), request.getMood()))
                .build();
    }

    // llm으로 취미 확인 메세지 생성
    @Override
    public BasicChatResponse generateCheckHobbyMessage(Long customerId, String name, String currentHobby) {
        // FAST API 요청 DTO 생성
        CheckHobbyRequest request = CheckHobbyRequest.of(customerId, name, currentHobby);

//        return fastApiClient.post("/hobby-check", request, BasicChatResponse.class);
        return BasicChatResponse.builder()
                .message(String.format("평소 관심있던 %s로 뭔가 찾아볼까?", request.getCurrentHobby()))
                .build();
    }

    // llm으로 취미 변경 메세지 생성
    @Override
    public BasicChatResponse generateUpdateHobbyMessage(Long customerId, String name, String newHobby) {
        UpdateHobbyRequest request = UpdateHobbyRequest.of(customerId, name, newHobby);

//        return fastApiClient.post("/hobby-update", request, BasicChatResponse.class);
        return BasicChatResponse.builder()
                .message(String.format("%s에 관심생겼구나! 좋은 선택이야~", request.getNewHobby()))
                .build();
    }

    // llm으로 최근 구매 카테고리 확인 메세지 생성
    @Override
    public BasicChatResponse generatePurchasesAnalyzeMessage(Long customerId, String name, String category) {
        PurchasesAnalyzeRequest request = PurchasesAnalyzeRequest.of(customerId, name, category);

//        return fastApiClient.post("/analyze-purchases", request, BasicChatResponse.class);
        return BasicChatResponse.builder()
                .message(String.format("%s이가 최근에 뭘 샀는지 파악하는 중이야~ %s 카테고리를 구매했네? 새로운 관심사랑 잘 맞을 것 같아!", request.getName(), request.getRecentPurchasesCategory()))
                .build();
    }

    // llm으로 추천 메세지 생성
    @Override
    public BasicChatResponse generateRecommendMessage(Long customerId, String name, ProductDto selectedProduct, String mood, String hobby) {
        RecommendMessageRequest request = RecommendMessageRequest.of(customerId, name, selectedProduct, ConsumerContextDto.of(mood, hobby));

//        return fastApiClient.post("/recommendation-message", request, BasicChatResponse.class);
        return BasicChatResponse.builder()
                .message(String.format("%s이가 %s에 관심생겼다니까 완전 찰떡인것 찾았어!", request.getName(), request.getConsumerContext().getHobby()))
                .build();
    }
}
