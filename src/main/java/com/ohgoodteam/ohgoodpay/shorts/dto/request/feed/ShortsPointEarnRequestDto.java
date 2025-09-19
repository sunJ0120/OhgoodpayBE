package com.ohgoodteam.ohgoodpay.shorts.dto.request.feed;

public record ShortsPointEarnRequestDto (
    Long customerId,
    int watchedSeconds,
    Long shortsId
){}
