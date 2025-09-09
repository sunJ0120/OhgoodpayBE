package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.ProductDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.util.List;

/**
 * FAST API - LLM 추천 메세지 & 상품 검색 결과 DTO
 *
 * LLM 추천 메세지 & 상품 검색 결과를 함께 담아두는 DTO
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class ChatRecommendResponse extends BaseChatResponse {
    private ProductDto product;
    private String message;
    private Boolean hasMore;
    private Integer remainingCount;
    private String nextStep;

    // 응답 dto 생성하는 정적 팩토리 메서드
    public static ChatRecommendResponse of(
            ProductDto selectedProduct,
            String message,
            List<ProductDto> allProducts,
            String nextStep) {

        return ChatRecommendResponse.builder()
                .product(selectedProduct)
                .message(message)
                .hasMore(allProducts.size() > 1)
                .remainingCount(allProducts.size() - 1)
                .nextStep(nextStep)
                .build();
    }
}
