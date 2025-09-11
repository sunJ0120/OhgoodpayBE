package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseLlmRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * FastAPI - LLM 채팅 시작 메세지 생성 요청 DTO
 *
 * 채팅 시작 후 메세지 생성 요청 데이터 전송 Request DTO
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class StartChatRequestDTO extends BaseLlmRequestDTO {
//    private Long customerId; 이거 이미 부모에 있음..왜 남겨뒀지
//    private String name;

    public static StartChatRequestDTO of(Long customerId, String name) {
        return StartChatRequestDTO.builder()
                .customerId(customerId)
                .name(name)
                .build();
    }
}
