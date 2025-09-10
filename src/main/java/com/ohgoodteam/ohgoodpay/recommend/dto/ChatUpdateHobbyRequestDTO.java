package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatRequestDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 취미 업데이트 채팅 메세지 생성 요청 DTO
 *
 * 새로운 취미를 받아서 채팅 메세지 생성을 요청하는 Response DTO
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class ChatUpdateHobbyRequestDTO extends BaseChatRequestDTO {
    private String newHobby;
    private String action; // TODO : 이 액션이 뭔지 정확하게 명시하기.
}
