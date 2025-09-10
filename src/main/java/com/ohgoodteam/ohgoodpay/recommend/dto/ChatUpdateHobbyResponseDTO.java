package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 취미 업데이트 확인 채팅 DTO
 *
 * 취미를 업데이트 한 후 채팅을 생성하기 위한 Response DTO
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class ChatUpdateHobbyResponseDTO extends BaseChatResponseDTO {
    private String updatedHobby;

    public static ChatUpdateHobbyResponseDTO of(String message, String newHobby, String nextStep) {
        return ChatUpdateHobbyResponseDTO.builder()
                .message(message)
                .updatedHobby(newHobby)
                .nextStep(nextStep)
                .build();
    }
}
