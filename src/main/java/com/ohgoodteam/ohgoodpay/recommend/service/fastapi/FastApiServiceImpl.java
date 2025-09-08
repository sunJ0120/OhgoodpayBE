package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/*
  fastapi 서버와 통신하는 서비스
 */
@Service
@RequiredArgsConstructor
public class FastApiServiceImpl implements FastApiService {
//    @Value("${fastapi.base-url}") //fastApiBaseUrl 미리 불러오기
//    private String fastApiBaseUrl;

    // 검색 키워드 생성
    @Override
    public KeywordGenerateResponse generateKeywords(KeywordGenerateRequest request) {
        // TODO: 나중에 FastAPI 호출, 지금은 Mock
        return KeywordGenerateResponse.builder()
                .keyword("요리 도구")
                .priceRange("10000-40000")
                .build();
    }

    // naver shopping api로 상품 검색 결과
    @Override
    public ProductSearchResponse searchProducts(ProductSearchRequest request) {
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

        return ProductSearchResponse.builder()
                .products(mockProducts)
                .build();
    }

    // llm으로 추천 메세지 생성
    @Override
    public RecommendMessageResponse generateRecommendMessage(RecommendMessageRequest request) {
        String responseMessage = String.format("%s이가 %s에 관심생겼다니까 완전 찰떡인것 찾았어!", request.getUsername(), request.getComsumerContextDto().getHobby());

        return RecommendMessageResponse.builder()
                .message(responseMessage)
                .build();
    }
}
