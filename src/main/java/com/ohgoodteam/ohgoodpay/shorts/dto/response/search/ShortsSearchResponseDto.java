package com.ohgoodteam.ohgoodpay.shorts.dto.response.search;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ShortsSearchResponseDto {
    public record LayoutItem(Long shortsId, String thumbnail, Long likeCount){};
    public record CursorResponse(
        List<LayoutItem> items,
        NextCursor nextCursor,
        boolean hasNext // 다음 검색이 있는지
    ) {
        public record NextCursor(Long lastId, LocalDateTime lastDate, BigDecimal lastScore) {}
        // 다음 검색 시작점
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShortsSearchResponse {
        private Long shortsId;
        private String thumbnail;
        private Long likeCount;
        private LocalDateTime date;
        private BigDecimal score;
    }
}
