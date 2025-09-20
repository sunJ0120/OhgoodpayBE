package com.ohgoodteam.ohgoodpay.shorts.dto.request.profile;

import org.springframework.web.multipart.MultipartFile;

public record ShortsProfileEditRequestDto(
    String nickname,
    String introduce,
    MultipartFile profileImg
){}