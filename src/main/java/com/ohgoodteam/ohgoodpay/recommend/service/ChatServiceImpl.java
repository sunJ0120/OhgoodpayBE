package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.recommend.dto.*;
import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDto;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.*;
import com.ohgoodteam.ohgoodpay.recommend.service.fastapi.FastApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final CustomerRepository customerRepository;
    private final ChatCacheService chatCacheService;
    private final FastApiService fastApiService;

    /**
     * 채팅 시작 처리
     * 고객명 조회 → 인사 메시지 생성
     */
    @Override
    @Transactional(readOnly = true)
    public ChatStartResponse startChat(Long customerId) {
        CustomerCacheDto cacheDto = chatCacheService.getCustomerInfo(customerId);
        String name = cacheDto.getName();
        
        // TODO: FastAPI 연동 구현
        String greetingMessage = String.format("안녕 나는 너만의 오레이봇봇 ~ 나를 레이라고 불러줘 %s~ 오늘 기분은 어때?", name);
        String nextStep = "mood_input";

        return ChatStartResponse.builder()
                .message(greetingMessage)
                .nextStep(nextStep)
                .build();
    }

    /**
     * 고객 기분 입력받아서 llm요청
     * 고객명 조회 → 기분 메시지 생성
     */
    @Override
    @Transactional(readOnly = true)
    public ChatMoodResponse moodChat(Long customerId, String mood) {
        CustomerCacheDto cacheDto = chatCacheService.getCustomerInfo(customerId);
        String name = cacheDto.getName();

        // TODO: Redis 캐싱 구현 (mood만 저장)

        // TODO: FastAPI 연동 구현
        String greetingMessage = String.format("%s이가 기분이 %s하다니 나도 좋은걸~ 그럼 오늘 뭐가 필요한지 알아볼까?", name, mood);
        String nextStep = "hobby_check";

        return ChatMoodResponse.builder()
                .message(greetingMessage)
                .nextStep(nextStep)
                .build();
    }

    /**
     * 고객 아이디 입력받아서 llm요청
     * 고객명 조회 → 취미 확인하는 메시지 생성
     */
    @Override
    @Transactional(readOnly = true)
    public ChatCheckHobbyResponse checkHobby(Long customerId) {
        CustomerCacheDto cacheDto = chatCacheService.getCustomerInfo(customerId);
        String name = cacheDto.getName();
        String hobby = chatCacheService.getHobby(customerId);

        // TODO: FastAPI 연동 구현
        String llmMessage = String.format("평소 관심있던 %s로 뭔가 찾아볼까?", hobby);
        String nextStep = "hobby_confirm";

        return ChatCheckHobbyResponse.builder()
                .message(llmMessage)
                .currentHobbies(hobby)
                .nextStep(nextStep)
                .build();
    }

    /**
     * 고객 취미 업데이트
     * DB 업데이트 → 응답 생성
     */
    @Override
    public ChatUpdateHobbyResponse updateHobby(Long customerId, String newHobby) {
        CustomerCacheDto cacheDto = chatCacheService.getCustomerInfo(customerId);
        // String name = cacheDto.getName();

        // TODO: Redis 캐싱 구현 (hobby만 저장)
        
        // DB 업데이트 실행
        int updatedRows = customerRepository.updateHobbyByCustomerId(customerId, newHobby);
        
        if (updatedRows == 0) {
            throw new IllegalStateException("취미 업데이트에 실패했습니다");
        }

        // TODO: FastAPI 연동 구현
        String responseMessage = String.format("%s에 관심생겼구나! 좋은 선택이야~", newHobby);
        String nextStep = "analyzing_purchases";

        return ChatUpdateHobbyResponse.builder()
                .message(responseMessage)
                .updatedHobby(newHobby)
                .nextStep(nextStep)
                .build();
    }

    /**
     * 고객 구매 이력 (카테고리) 가져오기
     * 고객명 조회 → DB또는 REDIS에서 구매 카테고리 가져오기 → 기분 메시지 생성
     */
    @Override
    public ChatAnalyzePurchasesResponse analyzePurchases(Long customerId) {
        CustomerCacheDto cacheDto = chatCacheService.getCustomerInfo(customerId);
        String name = cacheDto.getName();
        String category = chatCacheService.getRecentPurchaseCategory(customerId);

        // TODO: FastAPI 연동 구현
        String responseMessage = String.format("%s이가 최근에 뭘 샀는지 파악하는 중이야~ %s 카테고리를 구매했네? 새로운 관심사랑 잘 맞을 것 같아!", name, category);
        String nextStep = "recommendation_ready";

        return ChatAnalyzePurchasesResponse.builder()
                .message(responseMessage)
                .analyzedCategory(category)
                .nextStep(nextStep)
                .build();
    }

    /**
     * 고객한테 상품 추천 하기
     * 고객명 조회 → DB또는 REDIS에서 상위 1등 상품만 가져오기 → 추천 메시지 생성
     */
    @Override
    public ChatRecommendResponse recommend(Long customerId) {
        // 캐싱 되어 있는 값들 전부 가져오기
        CustomerCacheDto cacheDto = chatCacheService.getCustomerInfo(customerId);
        String hobby = chatCacheService.getHobby(customerId);
        String mood = chatCacheService.getMood(customerId);
        String category = chatCacheService.getRecentPurchaseCategory(customerId);
        Integer balance = chatCacheService.getBalance(customerId);

        KeywordGenerateRequest keywordGenerateRequest = KeywordGenerateRequest.builder()
                .customerInfo(cacheDto)
                .hobby(hobby)
                .mood(mood)  // 기분 정보는 현재 저장되지 않음
                .category(category)
                .balance(balance)
                .build();

        // 키워드 생성
        KeywordGenerateResponse keywordGenerateResponse = fastApiService.generateKeywords(keywordGenerateRequest);

        // 상품 가져오기
        ProductSearchResponse productSearchResponse = fastApiService.searchProducts( ProductSearchRequest.builder()
                        .keyword(keywordGenerateResponse.getKeyword())
                        .priceRange(keywordGenerateResponse.getPriceRange())
                        .maxResults(5)
                .build());

        // topN개의 products 캐싱
        chatCacheService.saveRecommendProducts(customerId, productSearchResponse.getProducts());
        // 1등 상품 직접 선택
        ProductDto selectedProduct = productSearchResponse.getProducts().get(0);

        // TODO : FastAPI 연동 구현 - LLM 메세지 생성
        String responseMessage = String.format("%s가 %s에 관심생겼다니까 완전 찰떡인 %s 찾았어!", cacheDto.getName(), hobby, selectedProduct.getName());
        String nextStep = "get_next_recommendation";

        //최종 응답 DTO 생성
        // TODO : 현재는 하드코딩 이지만, 캐시에서 하나씩 빼서 size로 판단
        ChatRecommendResponse chatRecommendResponse = ChatRecommendResponse.builder()
                .item(selectedProduct)
                .message(responseMessage)
                .hasMore(productSearchResponse.getProducts().size() > 1)
                .remainingCount(productSearchResponse.getProducts().size() - 1)
                .nextStep(nextStep)
                .build();

        return chatRecommendResponse;
    }
}
