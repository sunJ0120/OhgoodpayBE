package com.ohgoodteam.ohgoodpay.shorts.dto.response.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortsCommentDeleteDataDTO {
    private Long commentId;
    private Long shortsId;
    private Long customerId;
    private boolean deleted;
}
