package com.ohgoodteam.ohgoodpay.shorts.dto.response.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortsFeedListDataDto {
    private List<ShortsFeedDataDto> shortsFeedList; // 댓글 리스트
    private boolean hasNext; // 다음 페이지 여부
}
