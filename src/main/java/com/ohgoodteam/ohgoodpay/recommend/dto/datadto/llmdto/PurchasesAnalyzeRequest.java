package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseLlmRequest;
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
public class PurchasesAnalyzeRequest extends BaseLlmRequest {
    private String recentPurchasesCategory;

    public static PurchasesAnalyzeRequest of(Long customerId, String name, String recentPurchasesCategory) {
        return PurchasesAnalyzeRequest.builder()
                .customerId(customerId)
                .name(name)
                .recentPurchasesCategory(recentPurchasesCategory)
                .build();
    }
}
