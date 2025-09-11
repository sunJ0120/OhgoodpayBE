package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseLlmRequestDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.ConsumerContextDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.ProductDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * FastAPI - LLM 상품 추천 메세지 생성 요청 DTO
 *
 * 상품 추천 메세지 생성 요청 데이터 전송 Request DTO
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class RecommendMessageRequestDTO extends BaseLlmRequestDTO {
    private ProductDTO product;
    private ConsumerContextDTO consumerContext;

    public static RecommendMessageRequestDTO of(Long customerId, String name, ProductDTO product, ConsumerContextDTO consumerContext) {
        return RecommendMessageRequestDTO.builder()
                .customerId(customerId)
                .name(name)
                .product(product)
                .consumerContext(consumerContext)
                .build();
    }
}
