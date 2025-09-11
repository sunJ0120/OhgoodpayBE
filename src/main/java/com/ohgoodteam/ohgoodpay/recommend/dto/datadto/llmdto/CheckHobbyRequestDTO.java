package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseLlmRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * FastAPI - LLM 취미 확인 메세지 생성 요청 DTO DTO
 *
 * 캐싱된 취미 확인 메세지 생성 요청 데이터 전송 Request DTO
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class CheckHobbyRequestDTO extends BaseLlmRequestDTO {
    private String currentHobby;

    public static CheckHobbyRequestDTO of(Long customerId, String name, String currentHobby) {
        return CheckHobbyRequestDTO.builder()
                .customerId(customerId)
                .name(name)
                .currentHobby(currentHobby)
                .build();
    }
}
