package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.recommend.dto.*;
import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.*;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.KeywordGenerateResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.ProductDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.ProductSearchResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.service.fastapi.LlmService;
import com.ohgoodteam.ohgoodpay.recommend.service.fastapi.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 채팅 껍데기 서버 서비스 구현체
 *
 * 고객과의 실시간 채팅, LLM 응답 생성, 추천 서비스 연동을 담당
 * Redis 캐시를 통한 성능 최적화 및 외부 FastAPI 서버와의 통신 처리
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final CustomerRepository customerRepository; // DB 직접 접근용 (update)
    private final ChatCacheService chatCacheService; // 캐싱 서비스
    private final LlmService llmService; // FastAPI LLM 연동 서비스
    private final RecommendationService recommendationService; // FastAPI 추천 연동 서비스

    /**
     * 채팅 시작 처리
     * cacheDto에서 고객명 조회 → 인사 메시지 생성
     */
    @Override
    @Transactional(readOnly = true)
    public ChatMessageResponseDTO startChat(Long customerId) {
        // 캐싱된 고객 기본 정보 조회 (customerId, name, balance)
        CustomerCacheDTO cacheDto = chatCacheService.getCustomerInfo(customerId);
        String name = cacheDto.getName();

        // RESTFUL 한 설계를 위해, FAST API 연동 결과도 DTO로 감쌈
        BasicChatResponseDTO response = llmService.generateStartMessage(customerId, name);
        String nextStep = "mood_input";

        return ChatMessageResponseDTO.of(response.getMessage(), nextStep);
    }

    /**
     * 고객 기분 입력
     * cacheDto에서 고객명 조회 → 기분 메시지 생성
     */
    @Override
    @Transactional(readOnly = true)
    public ChatMessageResponseDTO moodChat(Long customerId, String mood) {
        CustomerCacheDTO cacheDto = chatCacheService.getCustomerInfo(customerId);
        String name = cacheDto.getName();

        // TODO: Redis 캐싱 구현 (mood만 저장)

        BasicChatResponseDTO response = llmService.generateInputMoodMessage(customerId, name, mood);
        String nextStep = "hobby_check";

        return ChatMessageResponseDTO.of(response.getMessage(), nextStep);
    }

    /**
     * 고객 취미 확인
     * cacheDto에서 고객명 조회 → 취미 확인하는 메시지 생성
     */
    @Override
    @Transactional(readOnly = true)
    public ChatCheckHobbyResponseDTO checkHobby(Long customerId) {
        CustomerCacheDTO cacheDto = chatCacheService.getCustomerInfo(customerId);
        String name = cacheDto.getName();
        String currentHobby = chatCacheService.getHobby(customerId); //원래 저장되어 있었던 취미

        BasicChatResponseDTO response = llmService.generateCheckHobbyMessage(customerId, name, currentHobby);
        String nextStep = "hobby_confirm";

        return ChatCheckHobbyResponseDTO.of(response.getMessage(), currentHobby, nextStep);
    }

    /**
     * 고객 취미 업데이트
     * cacheDto에서 고객명 조회 → DB 업데이트 → 응답 생성
     */
    @Override
    public ChatUpdateHobbyResponseDTO updateHobby(Long customerId, String newHobby) {
        CustomerCacheDTO cacheDto = chatCacheService.getCustomerInfo(customerId);
        String name = cacheDto.getName();
        //  String previousHobby = chatCacheService.getHobby(customerId); // 기존 취미, 메세지 확장 필요할때 사용

        // TODO: Redis 캐싱 구현 (newHobby 저장)
        
        // DB 업데이트 실행
        int updatedRows = customerRepository.updateHobbyByCustomerId(customerId, newHobby);
        
        if (updatedRows == 0) {
            throw new IllegalStateException("취미 업데이트에 실패했습니다");
        }

        BasicChatResponseDTO response = llmService.generateUpdateHobbyMessage(customerId, name, newHobby);
        String nextStep = "analyzing_purchases";

        return ChatUpdateHobbyResponseDTO.of(response.getMessage(), newHobby, nextStep);
    }

    /**
     * 고객 구매 이력 (카테고리) 가져오기
     * 고객명 조회 → DB또는 REDIS에서 구매 카테고리 가져오기 → 기분 메시지 생성
     */
    @Override
    @Transactional(readOnly = true)
    public ChatAnalyzePurchasesResponseDTO analyzePurchases(Long customerId) {
        CustomerCacheDTO cacheDto = chatCacheService.getCustomerInfo(customerId);
        String name = cacheDto.getName();
        String category = chatCacheService.getRecentPurchaseCategory(customerId);

        BasicChatResponseDTO response = llmService.generatePurchasesAnalyzeMessage(customerId, name, category);
        String nextStep = "recommendation_ready";

        return ChatAnalyzePurchasesResponseDTO.of(response.getMessage(), category, nextStep);
    }

    /**
     * 고객한테 상품 추천 하기
     * 고객명 조회 → DB또는 REDIS에서 상위 1등 상품만 가져오기 → 추천 메시지 생성
     */
    @Override
    @Transactional(readOnly = true)
    public ChatRecommendResponseDTO recommend(Long customerId) {
        // 캐싱 되어 있는 값들 전부 가져오기
        CustomerCacheDTO cacheDto = chatCacheService.getCustomerInfo(customerId);
        String hobby = chatCacheService.getHobby(customerId);
        String mood = chatCacheService.getMood(customerId);
        String category = chatCacheService.getRecentPurchaseCategory(customerId);
        Integer balance = chatCacheService.getBalance(customerId);

        // 키워드 생성
        KeywordGenerateResponseDTO keywordGenerateResponseDTO = recommendationService.generateKeywords(cacheDto, hobby, mood, category, balance);

        // 상품 가져오기
        ProductSearchResponseDTO productSearchResponseDTO = recommendationService.searchProducts(
                keywordGenerateResponseDTO.getKeyword(), keywordGenerateResponseDTO.getPriceRange());

        // 상품 검색 결과 검증
        if (productSearchResponseDTO.getProducts().isEmpty()) {
            throw new IllegalStateException("추천할 상품이 없습니다");
        }

        // topN개의 products 캐싱
        chatCacheService.saveRecommendProducts(customerId, productSearchResponseDTO.getProducts());
        // 1등 상품 직접 선택
        ProductDTO selectedProduct = productSearchResponseDTO.getProducts().get(0);

        // 추천 메시지 생성
        BasicChatResponseDTO response = llmService.generateRecommendMessage(customerId, cacheDto.getName(), selectedProduct, mood, hobby);
        String nextStep = "get_next_recommendation";

        // 최종 응답 DTO 생성
        // TODO : 현재는 하드코딩 이지만, 캐시에서 하나씩 빼서 size로 판단
        return ChatRecommendResponseDTO.of(selectedProduct, response.getMessage(), productSearchResponseDTO.getProducts(), nextStep);
    }
}
