package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseLlmRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * FastAPI - LLM 기분 메세지 생성 요청 DTO
 *
 * 기분 입력 후 메세지 생성 요청 데이터 전송 Request DTO
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class InputMoodRequestDTO extends BaseLlmRequestDTO {
    private String mood;

    public static InputMoodRequestDTO of(Long customerId, String name, String mood) {
        return InputMoodRequestDTO.builder()
                .customerId(customerId)
                .name(name)
                .mood(mood)
                .build();
    }
}
