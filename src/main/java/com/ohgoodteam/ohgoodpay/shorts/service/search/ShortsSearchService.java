package com.ohgoodteam.ohgoodpay.shorts.service.search;

import java.time.LocalDateTime;

import com.ohgoodteam.ohgoodpay.shorts.dto.response.search.ShortsSearchResponseDto.CursorResponse;

public interface ShortsSearchService {
    CursorResponse getFeed(
            String q,
            Integer limit,
            Long lastId,
            LocalDateTime lastDate,
            Double lastScore
    );
}
