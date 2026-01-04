package com.ohgoodteam.ohgoodpay.shorts.service.search;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.search.ShortsSearchResponseDTO.CursorResponse;

public interface ShortsSearchService {
    
    /**
     * 쇼츠 검색 피드를 조회
     * 
     * @param q 검색어 (null 또는 빈 문자열인 경우 전체 피드 조회)
     * @param limit 조회할 피드 개수 제한 (기본값: 24, 최대: 50)
     * @param lastId 마지막으로 조회된 쇼츠 ID (페이징용)
     * @param lastDate 마지막으로 조회된 쇼츠의 생성일시 (페이징용)
     * @param lastScore 마지막으로 조회된 쇼츠의 점수 (페이징용)
     * @return 검색 결과와 다음 페이지 커서 정보를 포함한 응답
     */
    CursorResponse getFeed(
            String q,
            Integer limit,
            Long lastId,
            LocalDateTime lastDate,
            BigDecimal lastScore
    );
}
