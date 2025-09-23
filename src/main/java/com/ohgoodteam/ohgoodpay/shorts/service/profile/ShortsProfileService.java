package com.ohgoodteam.ohgoodpay.shorts.service.profile;

import com.ohgoodteam.ohgoodpay.shorts.dto.response.profile.ShortsProfileDataDto;
import org.springframework.web.multipart.MultipartFile;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.profile.ShortsProfileEditResponseDto;

public interface ShortsProfileService {

    /**
     * 특정 유저의 프로필 정보 조회
     * @param targetId
     * @param page
     * @param sortBy
     * @return
     */
    ShortsProfileDataDto getProfile(Long targetId, int page, String sortBy);

    /**
     * 구독 요청
     */
    long subscribe(Long customerId, Long targetId);

 
    // 프로필 편집
    ShortsProfileEditResponseDto editProfile(Long customerId, String nickname, String introduce, MultipartFile profileImg);
}
