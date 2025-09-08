package com.ohgoodteam.ohgoodpay.shorts.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class ShortsSearchResponseDto {
    public record LayoutItem(Long shortsId, String thumbnail, Long likeCount){};
    public record CursorResponse(
        List<LayoutItem> items,
        NextCursor nextCursor,
        boolean hasNext // 다음 검색이 있는지
    ) {
        public record NextCursor(Long lastId, LocalDateTime lastDate, Double lastScore) {}
        // 다음 검색 시작점
    }
    public interface ShortsSearchResponse { 
        // dto->entity 변환 필요없이 Protection 인터페이스 기반 DTO 매핑 (ShortsEntity 전체 컬럼 가져올 필요x)
        Long getShortsId();
        String getThumbnail();
        Long getLikeCount();
        LocalDateTime getDate();
        Double getScore();
    }
}
