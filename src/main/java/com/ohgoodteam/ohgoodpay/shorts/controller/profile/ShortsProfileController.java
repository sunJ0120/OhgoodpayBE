package com.ohgoodteam.ohgoodpay.shorts.controller.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.profile.ShortsProfileEditResponseDto;
import com.ohgoodteam.ohgoodpay.shorts.service.profile.ShortsProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ShortsProfileController {
    private final ShortsProfileService shortsProfileService;

    // 프로필 편집
    @PostMapping("/profile/edit")
    public ResponseEntity<ShortsProfileEditResponseDto> editProfile(
        @RequestParam("customerId") Long customerId,
        @RequestParam("nickname") String nickname,
        @RequestParam("introduce") String introduce,
        @RequestPart(value = "profileImg", required = false) MultipartFile profileImg
    ) {
        return ResponseEntity.ok(shortsProfileService.editProfile(customerId, nickname, introduce, profileImg));
    }
}
