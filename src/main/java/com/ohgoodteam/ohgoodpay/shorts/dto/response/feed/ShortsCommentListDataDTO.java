package com.ohgoodteam.ohgoodpay.shorts.dto.response.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortsCommentListDataDTO {
    private List<ShortsCommentDataDTO> comments; // 댓글 리스트
}
