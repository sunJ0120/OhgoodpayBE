package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseLlmResponseDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * FAST API - LLM 응답 기본 DTO
 *
 * LLM이 주는 챗봇 응답 메세지를 담는 DTO
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class BasicChatResponseDTO extends BaseLlmResponseDTO {
}
