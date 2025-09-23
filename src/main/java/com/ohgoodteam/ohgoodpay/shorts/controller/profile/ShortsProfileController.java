package com.ohgoodteam.ohgoodpay.shorts.controller.profile;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.ApiResponseWrapper;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.profile.ShortsProfileDataDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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


    /**
     * 특정 유저의 프로필 정보 조회
     * @param targetId
     * @param page
     * @param sortBy
     * @return
     */
    @GetMapping("/profile")
    public ApiResponseWrapper<ShortsProfileDataDto> getProfile(
            @RequestParam (value = "targetId") Long targetId,
            @RequestParam (value = "page") int page,
            @RequestParam (value = "sortBy", defaultValue = "latest") String sortBy
    ){
        try {
            ShortsProfileDataDto dto =  shortsProfileService.getProfile(targetId,page,sortBy);
            return ApiResponseWrapper.ok(dto);
        }
        catch (IllegalArgumentException e){
            return ApiResponseWrapper.error(400, e.getMessage());
        }
    }

    /**
     * 구독 요청
     */
    @PostMapping("/subscribe/{targetId}")
    public ApiResponseWrapper<String> subscribe(
        @PathVariable Long targetId
    ){
        Long cutomerId = 1L; // TODO: 인증 로직 후 수정
        // + 자기 자신 구독 방지
        if(cutomerId.equals(targetId)){
            return ApiResponseWrapper.error(400, "자기 자신은 구독할 수 없습니다.");
        }

        try {
            long result = shortsProfileService.subscribe(cutomerId, targetId);

            if(result == 0){
                return ApiResponseWrapper.error(400, "이미 구독한 사용자입니다.");
            }
            return ApiResponseWrapper.ok("구독 성공");
        } catch (IllegalArgumentException e){
            return ApiResponseWrapper.error(400, e.getMessage());
        }
    }
}
