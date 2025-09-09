package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseLlmRequest;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.ConsumerContextDto;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.ProductDto;
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
public class RecommendMessageRequest extends BaseLlmRequest {
    private ProductDto product;
    private ConsumerContextDto consumerContext;

    public static RecommendMessageRequest of(Long customerId, String name, ProductDto product, ConsumerContextDto consumerContext) {
        return RecommendMessageRequest.builder()
                .customerId(customerId)
                .name(name)
                .product(product)
                .consumerContext(consumerContext)
                .build();
    }
}
