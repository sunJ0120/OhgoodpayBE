package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDto;
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
    public KeywordGenerateResponse generateKeywords(CustomerCacheDto cacheDto, String hobby, String mood, String category, Integer balance) {
        KeywordGenerateRequest request = KeywordGenerateRequest.of(cacheDto, hobby, mood, category, balance);

        return fastApiClient.post("/recommend/generate-keyword", request, KeywordGenerateResponse.class);
    }

    // naver shopping api로 상품 검색 결과
    @Override
    public ProductSearchResponse searchProducts(String keyword, String priceRange) {
        //top 5개만 가져오도록 구성한다.
        ProductSearchRequest request = ProductSearchRequest.of(keyword, priceRange, 5);

        // TODO: 나중에 FastAPI 호출, 지금은 Mock
        List<ProductDto> mockProducts = Arrays.asList(
                ProductDto.builder()
                        .rank(1)
                        .name("쿠킹 스푼 세트")
                        .price(15000)
                        .image("http://example.com/image1.jpg")
                        .url("http://example.com/product1")
                        .category("주방용품")
                        .build(),
                ProductDto.builder()
                        .rank(2)
                        .name("논스틱 프라이팬")
                        .price(30000)
                        .image("http://example.com/image2.jpg")
                        .url("http://example.com/product2")
                        .category("주방용품")
                        .build(),
                ProductDto.builder()
                        .rank(3)
                        .name("스테인레스 냄비")
                        .price(45000)
                        .image("http://example.com/image3.jpg")
                        .url("http://example.com/product3")
                        .category("주방용품")
                        .build()
        );

//        return fastApiClient.post("/product/generate-keywords", request, ProductSearchResponse.class);

        return ProductSearchResponse.builder()
                .products(mockProducts)
                .build();
    }
}
