package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class DashSayMyNameResponseDTO extends BaseChatResponseDTO { // userId 만 가져오는 dto
    /** 후속 대화/추적용 세션 ID */
    private String sessionId;

    /** 세션 TTL(초) — FastAPI가 준 값을 그대로 전달 */
    private Integer ttlSeconds;

    @JsonProperty("ohgoodScore")
    private Integer ohgoodScore;

    /** 스프링이 DB에서 집계) */
    private boolean extensionThisMonth;       // 이번달 연장 여부
    private boolean autoExtensionThisMonth;   // 이번달 자동연장 여부
    private int     autoExtensionCnt12m;      // 최근 12개월 자동연장 횟수
    private int     gradePoint;               // 등급점수(0~150)
    private boolean blocked;                  // 제재 여부
    private int     paymentCnt12m;            // 최근 12개월 결제 횟수
    private double  paymentAmount12m;         // 최근 12개월 결제 금액
    private double  currentCycleSpend;        // 이번달 결제 금액(0이면 12개월 평균/12 대체)
}
