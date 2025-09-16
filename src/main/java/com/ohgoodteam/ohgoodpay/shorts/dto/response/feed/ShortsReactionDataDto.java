package com.ohgoodteam.ohgoodpay.shorts.dto.response.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortsReactionDataDto {
    private Long shortsId; // 쇼츠 ID
    private int likeCount; // 좋아요 수
    private String myReaction; // 현재 사용자의 반응 (like, dislike, none)
}
