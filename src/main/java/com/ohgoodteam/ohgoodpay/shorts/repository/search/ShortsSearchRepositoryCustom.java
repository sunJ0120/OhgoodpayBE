package com.ohgoodteam.ohgoodpay.shorts.repository.search;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.search.ShortsSearchResponseDTO.ShortsSearchResponse;

public interface ShortsSearchRepositoryCustom {
    
    //검색 결과 조회 (가중치 로직, 점수 계산, 정렬, 페이징 처리)
    List<ShortsSearchResponse> fetchExposure(
        String q,
        double wLike,
        double wComment,
        double wHashtag,
        double wRecency,
        double tauHours,
        BigDecimal lastScore,
        LocalDateTime lastDate,
        Long lastId,
        int limitPlusOne
    );
}
