package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 현재 취미 확인 채팅 DTO
 *
 * 현재 취미를 확인하는 채팅을 위한 Response DTO
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class ChatCheckHobbyResponse extends BaseChatResponse {
    private String currentHobby;

    public static ChatCheckHobbyResponse of(String message, String currentHobby, String nextStep) {
        return ChatCheckHobbyResponse.builder()
                .message(message)
                .currentHobby(currentHobby)
                .nextStep(nextStep)
                .build();
    }
}
