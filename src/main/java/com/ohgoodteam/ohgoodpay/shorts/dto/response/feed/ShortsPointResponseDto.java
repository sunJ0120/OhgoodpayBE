package com.ohgoodteam.ohgoodpay.shorts.dto.response.feed;

public record ShortsPointResponseDto (
    int todayAccumSec,            // 오늘 누적 시청초(계산값)
    int todayPoint,         // 오늘 적립 포인트(최대 100)
    int progressSec,  // 0~60 (게이지용)
    int limitPoint,                 // 100
    int pointPerLap,              // 10
    boolean isRewarded          // 이번 호출에서 새로 10p 지급됐는지
){}
