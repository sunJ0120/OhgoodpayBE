package com.ohgoodteam.ohgoodpay.shorts.dto.request.feed;

public record ShortsPointEarnRequestDTO(
    Long customerId,
    int watchedSeconds,
    Long shortsId
){}
