package com.ohgoodteam.ohgoodpay.shorts.dto.response.profile;

import com.ohgoodteam.ohgoodpay.shorts.enums.profile.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortsProfileDataDto {
    // 프로필 정보
    private Long customerId;
    private String customerNickname;
    private String introduce;
    private String profileImg;

    // 통계 정보 (구독자 수, 동영상 수)
    private Long subscriberCount;
    private Long videoCount;
    private SubscriptionStatus isSubscribed; // 구독 여부

    // 쇼츠 리스트
    private List<ShortsProfileListDataDto> shortsList;
    private boolean hasNext; // 다음 페이지 여부

}
