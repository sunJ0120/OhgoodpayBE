package com.ohgoodteam.ohgoodpay.shorts.dto.response.feed;

public record ShortsPointEarnResponseDTO(
    int earnedPoints, // 이번에 적립된 포인트
    int todayTotalPoints, // 오늘 적립된 포인트 합계
    boolean success,
    String message
){}
