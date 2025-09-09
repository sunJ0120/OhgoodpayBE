package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 최근 구매한 카테고리 분석 결과 DTO
 *
 * 최근 구매한 카테고리 분석 한 후 채팅을 위한 Response DTO
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class ChatAnalyzePurchasesResponse extends BaseChatResponse {
    private String analyzedCategory;

    public static ChatAnalyzePurchasesResponse of(String message, String category, String nextStep) {
        return ChatAnalyzePurchasesResponse.builder()
                .message(message)
                .analyzedCategory(category)
                .nextStep(nextStep)
                .build();
    }
}

