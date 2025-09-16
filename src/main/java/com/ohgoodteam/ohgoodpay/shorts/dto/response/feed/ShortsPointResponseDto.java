package com.ohgoodteam.ohgoodpay.shorts.dto.response.feed;

public record ShortsPointResponseDto (
    int todayAccumSec,  // 오늘 누적 시청초
    int todayPoint,    // 오늘 적립 포인트(최대 100p)
    int progressSec,  // 0~59 (프론트 포인트게이지용)
    int limitPoint,   // 100p
    int pointPerLap,  // 10p
    boolean isRewarded  // 포인트 지급 받았는지
){}
