package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 기분 채팅 메세지 생성 요청 DTO
 *
 * 기분을 받아서 채팅 메세지 생성을 요청하는 Response DTO
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class ChatMoodRequestDTO extends BaseChatRequestDTO {
    private String mood;
}
