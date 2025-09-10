package com.ohgoodteam.ohgoodpay.shorts.controller.mypage;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDto.ShelfPageResponse;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDto.UserCard;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDto.VideoCard;
import com.ohgoodteam.ohgoodpay.shorts.service.mypage.ShortsMypageService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class ShortsMypageController {

    private final ShortsMypageService shortsMypageService;
    
    @GetMapping("/{userId}/overview")
    public ShortsMypageResponseDto getOverview(@PathVariable Long userId, @RequestParam(defaultValue="8") Integer limit) {
        return shortsMypageService.getOverview(userId, limit);
    }

    @GetMapping("/subscribe")
    public ShelfPageResponse<UserCard> getSubscription(
        @RequestParam Long userId,
        @RequestParam(required=false) String cursor,
        @RequestParam(defaultValue="8") Integer limit
    ) {
        return shortsMypageService.getSubscriptions(userId, cursor, limit);
    }

    @GetMapping("/all")
    public ShelfPageResponse<VideoCard> getLikedVideos(
        @RequestParam Long userId,
        @RequestParam(required=false) String cursor,
        @RequestParam(defaultValue="8") Integer limit
    ) {
        return shortsMypageService.getLikedVideos(userId, cursor, limit);
    }

    @GetMapping("/comments")
    public ShelfPageResponse<VideoCard> getCommentedVideos(
        @RequestParam Long userId,
        @RequestParam(required=false) String cursor,
        @RequestParam(defaultValue="8") Integer limit
    ) {
        return shortsMypageService.getCommentedVideos(userId, cursor, limit);
    }

}
