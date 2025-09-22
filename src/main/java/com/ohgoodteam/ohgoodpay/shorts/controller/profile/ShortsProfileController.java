package com.ohgoodteam.ohgoodpay.shorts.controller.profile;

import com.ohgoodteam.ohgoodpay.shorts.dto.response.ApiResponseWrapper;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.profile.ShortsProfileDataDto;
import com.ohgoodteam.ohgoodpay.shorts.service.profile.ShortsProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shorts")
public class ShortsProfileController {

    private final ShortsProfileService shortsProfileService;

    /**
     * 특정 유저의 프로필 정보 조회
     * @param customerId
     * @param page
     * @param sortBy
     * @return
     */
    @GetMapping("/profile")
    public ApiResponseWrapper<ShortsProfileDataDto> getProfile(
            @RequestParam (value = "customerId") Long customerId,
            @RequestParam (value = "page") int page,
            @RequestParam (value = "sortBy", defaultValue = "latest") String sortBy
    ){
        try {
            ShortsProfileDataDto dto =  shortsProfileService.getProfile(customerId,page,sortBy);
            return ApiResponseWrapper.ok(dto);
        }
        catch (IllegalArgumentException e){
            return ApiResponseWrapper.error(400, e.getMessage());
        }
    }

    /**
     * 구독 요청
     */
    @PostMapping("/subscribe")
    public ApiResponseWrapper<String> subscribe(
            @RequestParam Long cutomerId,
            @RequestParam Long targetId
    ){
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
