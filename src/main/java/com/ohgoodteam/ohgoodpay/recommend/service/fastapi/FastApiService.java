package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.*;

/**
 * FastAPI 연동 서비스
 */
public interface FastApiService {

    /**
     * 상품 추천 요청 api
     *
     * 1단계: 상품 키워드 생성
     * 2단계: 상품 검색
     * 3단계: 추천 메시지 생성
     */
    KeywordGenerateResponse generateKeywords(KeywordGenerateRequest request);
    ProductSearchResponse searchProducts(ProductSearchRequest request);
    RecommendMessageResponse generateRecommendMessage(RecommendMessageRequest request);
}