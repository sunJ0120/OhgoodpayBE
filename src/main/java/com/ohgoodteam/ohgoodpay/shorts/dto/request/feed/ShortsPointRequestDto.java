package com.ohgoodteam.ohgoodpay.shorts.dto.request.feed;

public record ShortsPointRequestDto (
    Long shortsId,
    boolean isPlaying,
    Double PlaybackPos
){}
