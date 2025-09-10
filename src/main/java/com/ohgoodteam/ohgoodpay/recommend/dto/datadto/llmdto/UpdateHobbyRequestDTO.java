package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseLlmRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * FastAPI - LLM 취미 업데이트 메세지 생성 요청 DTO
 *
 * 취미 업데이트 확인 메세지 생성 요청 데이터 전송 Request DTO
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class UpdateHobbyRequestDTO extends BaseLlmRequestDTO {
    private String newHobby;

    public static UpdateHobbyRequestDTO of(Long customerId, String name, String newHobby) {
        return UpdateHobbyRequestDTO.builder()
                .customerId(customerId)
                .name(name)
                .newHobby(newHobby)
                .build();
    }
}
