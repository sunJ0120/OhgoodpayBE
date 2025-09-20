package com.ohgoodteam.ohgoodpay.shorts.service.profile;

import org.springframework.web.multipart.MultipartFile;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.profile.ShortsProfileEditResponseDto;

public interface ShortsProfileService {

    // 프로필 편집
    ShortsProfileEditResponseDto editProfile(Long customerId, String nickname, String introduce, MultipartFile profileImg);
}
