package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 채팅 메세지 기본 DTO
 *
 * 응답에 LLM에서 사용된 채팅 메세지만 담기는 경우 사용하는 Response DTO
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class ChatMessageResponseDTO extends BaseChatResponseDTO {
    // 응답 dto를 위한 정적 팩토리 메서드
    public static ChatMessageResponseDTO of(String message, String nextStep) {
        return ChatMessageResponseDTO.builder()
                .message(message)
                .nextStep(nextStep)
                .build();
    }
}
