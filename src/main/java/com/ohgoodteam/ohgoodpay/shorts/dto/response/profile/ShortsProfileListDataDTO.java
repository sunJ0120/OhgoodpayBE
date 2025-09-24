package com.ohgoodteam.ohgoodpay.shorts.dto.response.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ShortsProfileListDataDTO {
    // 쇼츠 정보
    private Long shortsId;
    private Long likeCount;
    private String thumbnail;

    // 추가 정보
//    private LocalDateTime date;
}
