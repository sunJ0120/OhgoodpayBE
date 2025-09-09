package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseLlmRequest;
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
public class CheckHobbyRequest extends BaseLlmRequest {
    private String currentHobby;

    public static CheckHobbyRequest of(Long customerId, String name, String currentHobby) {
        return CheckHobbyRequest.builder()
                .customerId(customerId)
                .name(name)
                .currentHobby(currentHobby)
                .build();
    }
}
