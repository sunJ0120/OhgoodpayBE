package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.KeywordGenerateResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.ProductSearchResponseDTO;

/**
 * FAST API - LLM 추천 서비스 인터페이스
 *
 * v1: Mock 데이터로 구현, 향후 FastAPI 연동 예정
 */
public interface RecommendationService {

    // 상품 키워드 생성
    KeywordGenerateResponseDTO generateKeywords(CustomerCacheDTO cacheDto, String hobby, String mood, String category, Integer balance);
    // 상품 검색 - naver api 연동
    ProductSearchResponseDTO searchProducts(String keyword, String priceRange);
}
