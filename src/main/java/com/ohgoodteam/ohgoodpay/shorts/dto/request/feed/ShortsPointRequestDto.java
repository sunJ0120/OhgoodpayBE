package com.ohgoodteam.ohgoodpay.shorts.dto.request.feed;

public record ShortsPointRequestDto (
    Long shortsId,
    boolean isPlaying,
    Double PlaybackPos // 현재 동영상 재생 위치
){}
