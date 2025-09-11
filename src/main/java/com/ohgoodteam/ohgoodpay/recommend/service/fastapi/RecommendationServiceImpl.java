package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.*;
import com.ohgoodteam.ohgoodpay.recommend.util.FastApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

/**
 * FAST API - 추천 서비스 구현체
 *
 * v1: Mock 데이터로 구현, 향후 FastAPI 연동 예정
 */
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final FastApiClient fastApiClient;

    // 검색 키워드 생성
    @Override
    public KeywordGenerateResponseDTO generateKeywords(CustomerCacheDTO cacheDto, String hobby, String mood, String category, Integer balance) {
        KeywordGenerateRequestDTO request = KeywordGenerateRequestDTO.of(cacheDto, hobby, mood, category, balance);

        return fastApiClient.post("/recommend/generate-keyword", request, KeywordGenerateResponseDTO.class);
    }

    // naver shopping api로 상품 검색 결과
    @Override
    public ProductSearchResponseDTO searchProducts(String keyword, String priceRange) {
        //top 5개만 가져오도록 구성한다.
        ProductSearchRequestDTO request = ProductSearchRequestDTO.of(keyword, priceRange, 5);
        return fastApiClient.post("/recommend/search", request, ProductSearchResponseDTO.class);
    }
}
