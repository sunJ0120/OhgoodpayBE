package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.ProductDTO;
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
public class ChatRecommendResponseDTO extends BaseChatResponseDTO {
    private ProductDTO product;
    private String message;
    private Boolean hasMore;
    private Integer remainingCount;
    private String nextStep;

    // 응답 dto 생성하는 정적 팩토리 메서드
    public static ChatRecommendResponseDTO of(
            ProductDTO selectedProduct,
            String message,
            List<ProductDTO> allProducts,
            String nextStep) {

        return ChatRecommendResponseDTO.builder()
                .product(selectedProduct)
                .message(message)
                .hasMore(allProducts.size() > 1)
                .remainingCount(allProducts.size() - 1)
                .nextStep(nextStep)
                .build();
    }
}
