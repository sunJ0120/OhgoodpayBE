package com.ohgoodteam.ohgoodpay.shorts.service.profile;

import com.ohgoodteam.ohgoodpay.shorts.dto.response.profile.ShortsProfileDataDto;

public interface ShortsProfileService {

    /**
     * 특정 유저의 프로필 정보 조회
     * @param customerId
     * @param page
     * @param sortBy
     * @return
     */
    ShortsProfileDataDto getProfile(Long customerId, int page, String sortBy);

    /**
     * 구독 요청
     */
    long subscribe(Long customerId, Long targetId);

}
