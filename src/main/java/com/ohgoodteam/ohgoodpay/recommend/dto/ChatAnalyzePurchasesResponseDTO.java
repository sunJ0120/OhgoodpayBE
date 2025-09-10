package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatResponseDTO;
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
public class ChatAnalyzePurchasesResponseDTO extends BaseChatResponseDTO {
    private String analyzedCategory;

    public static ChatAnalyzePurchasesResponseDTO of(String message, String category, String nextStep) {
        return ChatAnalyzePurchasesResponseDTO.builder()
                .message(message)
                .analyzedCategory(category)
                .nextStep(nextStep)
                .build();
    }
}

