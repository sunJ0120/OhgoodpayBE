package com.ohgoodteam.ohgoodpay.recommend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class DashSayMyNameResponseDTO {
    private final String  message;
    private final String  sessionId;
    private final Integer ttlSeconds;
    private final Integer ohgoodScore;

    private final boolean extensionThisMonth;
    private final boolean autoExtensionThisMonth;
    private final Integer autoExtensionCnt12m;
    private final Integer gradePoint;
    private final boolean blocked;

    private final Integer paymentCnt12m;
    private final Double  paymentAmount12m;
    private final Double  currentCycleSpend;

    public static DashSayMyNameResponseDTO ofCore(String message, String sessionId, Integer ttlSeconds, Integer ohgoodScore) {
        return builder()
                .message(message)
                .sessionId(sessionId)
                .ttlSeconds(ttlSeconds)
                .ohgoodScore(ohgoodScore)
                .build();
    }
}
