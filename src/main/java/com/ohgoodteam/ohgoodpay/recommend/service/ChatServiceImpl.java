package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CachedMessageDTO;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
     * 채팅 처리
     */
    @Override
    @Transactional(readOnly = true)
    public BasicChatResponseDTO chat(Long customerId) {
        // TODO: Redis에 사용할 sessionId 생성 로직 필요
        String sessionId = "session-" + customerId; // 임시 sessionId
        CustomerCacheDTO customerInfo = chatCacheService.getCustomerInfo(customerId); //고객 기본 정보
        String hobby = chatCacheService.getHobby(customerId); //원래 저장되어 있었던 취미
        String mood = chatCacheService.getMood(customerId);
        Integer balance = chatCacheService.getBalance(customerId);

        // TODO: Redis에 저장된 이전 대화 내역 불러오기 로직 필요
        CachedMessageDTO cachedMessage = CachedMessageDTO.builder()
                .role("system")
                .message("이전 대화 내역 예시입니다.")
                .timeStamp(LocalDateTime.now().toString())
                .tokens(0)
                .build();

        List<CachedMessageDTO> cachedMessages = new ArrayList<>();
        cachedMessages.add(cachedMessage);

        // LLM 서비스에 채팅 생성 요청
        // 속 서버에 요청하기 위한 값들을 겉 서버에서 모아서 넘긴다.
        BasicChatResponseDTO response = llmService.generateChat(
                sessionId,
                customerInfo,
                hobby,
                mood,
                balance,
                cachedMessages
        );

        // fast api에서 온 DB 저장 신호 확인
        if (response.isShouldUpdateHobbyDB()) {
            // 신호가 왔다면, spring에서 DB에 저장
            customerRepository.updateHobbyByCustomerId(customerId, response.getNewHobby());
            log.info("취미 DB 업데이트 완료 - customerId: {}, hobby: {}",
                    customerId, response.getNewHobby());
        }

        return response;
    }
//
//    /**
//     * 고객한테 상품 추천 하기
//     * 고객명 조회 → DB또는 REDIS에서 상위 1등 상품만 가져오기 → 추천 메시지 생성
//     */
//    @Override
//    @Transactional(readOnly = true)
//    public ChatRecommendResponseDTO recommend(Long customerId) {
//        // 캐싱 되어 있는 값들 전부 가져오기
//        CustomerCacheDTO cacheDto = chatCacheService.getCustomerInfo(customerId);
//        String hobby = chatCacheService.getHobby(customerId);
//        String mood = chatCacheService.getMood(customerId);
//        String category = chatCacheService.getRecentPurchaseCategory(customerId);
//        Integer balance = chatCacheService.getBalance(customerId);
//
//        // 키워드 생성
//        KeywordGenerateResponseDTO keywordGenerateResponseDTO = recommendationService.generateKeywords(cacheDto, hobby, mood, category, balance);
//
//        // 상품 가져오기
//        ProductSearchResponseDTO productSearchResponseDTO = recommendationService.searchProducts(
//                keywordGenerateResponseDTO.getKeyword(), keywordGenerateResponseDTO.getPriceRange());
//
//        // 상품 검색 결과 검증
//        if (productSearchResponseDTO.getProducts().isEmpty()) {
//            throw new IllegalStateException("추천할 상품이 없습니다");
//        }
//
//        // topN개의 products 캐싱
//        chatCacheService.saveRecommendProducts(customerId, productSearchResponseDTO.getProducts());
//        // 1등 상품 직접 선택
//        ProductDTO selectedProduct = productSearchResponseDTO.getProducts().get(0);
//
//        // 추천 메시지 생성
//        BasicChatResponseDTO response = llmService.generateRecommendMessage(customerId, cacheDto.getName(), selectedProduct, mood, hobby);
//        String nextStep = "get_next_recommendation";
//
//        // 최종 응답 DTO 생성
//        // TODO : 현재는 하드코딩 이지만, 캐시에서 하나씩 빼서 size로 판단
//        return ChatRecommendResponseDTO.of(selectedProduct, response.getMessage(), productSearchResponseDTO.getProducts(), nextStep);
//    }
}
