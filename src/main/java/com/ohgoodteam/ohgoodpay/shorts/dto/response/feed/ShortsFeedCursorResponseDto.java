package com.ohgoodteam.ohgoodpay.shorts.dto.response.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortsFeedCursorResponseDTO {
    private List<ShortsFeedDataDTO> feeds; // 쇼츠 피드 리스트
    private NextCursor next; // 다음 커서 정보
    private boolean hasNext; // 다음 페이지 여부

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class NextCursor {
        private Long lastId; // 마지막 쇼츠 ID
        private LocalDateTime lastDate; // 마지막 쇼츠 날짜
        private Double lastScore; // 마지막 쇼츠 점수
    }
}



