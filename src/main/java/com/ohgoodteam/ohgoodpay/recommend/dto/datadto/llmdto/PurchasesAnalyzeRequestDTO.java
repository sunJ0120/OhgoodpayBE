package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseLlmRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * FastAPI - LLM 최근 구매한 카테고리 분석 메세지 생성 요청 DTO
 *
 * 최근 구매한 카테고리 분석 메세지 생성 요청 데이터 전송 Request DTO
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class PurchasesAnalyzeRequestDTO extends BaseLlmRequestDTO {
    private String recentPurchasesCategory;

    public static PurchasesAnalyzeRequestDTO of(Long customerId, String name, String recentPurchasesCategory) {
        return PurchasesAnalyzeRequestDTO.builder()
                .customerId(customerId)
                .name(name)
                .recentPurchasesCategory(recentPurchasesCategory)
                .build();
    }
}
