package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 채팅 메세지 생성 요청 DTO
 *
 * 처음 채팅 메세지 생성을 요청하는 Response DTO
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class ChatStartRequest extends BaseChatRequest {
}
