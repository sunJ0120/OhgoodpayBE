package com.ohgoodteam.ohgoodpay.recommend.dto.dashdto;

// SNAKE_CASE 설정을 쓰고 있으면 @JsonProperty 없이도 OK
// (spring.jackson.property-naming-strategy=SNAKE_CASE)
public record OhgoodScorePayload(
        Boolean extensionThisMonth,
        Boolean autoExtensionThisMonth,
        Integer autoExtensionCnt12m,
        Integer gradePoint,
        Boolean isBlocked,
        Integer paymentCnt12m,
        Long paymentAmount12m,
        Long currentCycleSpend,
        Long cycleLimit // 선택
) {}
