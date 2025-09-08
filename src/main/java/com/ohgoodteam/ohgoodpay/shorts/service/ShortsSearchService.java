package com.ohgoodteam.ohgoodpay.shorts.service;

import java.time.LocalDateTime;

import com.ohgoodteam.ohgoodpay.shorts.dto.response.ShortsSearchResponseDto.CursorResponse;

public interface ShortsSearchService {
    CursorResponse getFeed(
            String q,
            Integer limit,
            Long lastId,
            LocalDateTime lastDate,
            Double lastScore
    );
}
