package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class DashSayMyNameResponse extends BaseChatResponse { // userId 만 가져오는 dto
    /** 후속 대화/추적용 세션 ID */
    private String sessionId;

    /** 세션 TTL(초) — FastAPI가 준 값을 그대로 전달 */
    private Integer ttlSeconds;

    @JsonProperty("ohgoodScore")
    private Integer ohgoodScore;
}
