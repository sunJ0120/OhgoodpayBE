package com.ohgoodteam.ohgoodpay.shorts.controller.mypage;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class ShortsMypageController {

    private final ShortsMypageService shortsMypageService;
    
    // 마이페이지 미리보기
    @GetMapping("/{userId}/overview")
    public ShortsMypageResponseDto getOverview(@PathVariable Long userId, @RequestParam(defaultValue="8") Integer limit) {
        return shortsMypageService.getOverview(userId, limit);
    }

    // 구독 전체보기
    @GetMapping("/subscribe") 
    public ShelfPageResponse<UserCard> getSubscription(
        @RequestParam Long userId,
        @RequestParam(required=false) String cursor,
        @RequestParam(defaultValue="8") Integer limit
    ) {
        return shortsMypageService.getSubscriptions(userId, cursor, limit);
    }

    // 좋아요 한 영상 전체보기
    @GetMapping("/all") 
    public ShelfPageResponse<VideoCard> getLikedVideos(
        @RequestParam Long userId,
        @RequestParam(required=false) String cursor,
        @RequestParam(defaultValue="8") Integer limit
    ) {
        return shortsMypageService.getLikedVideos(userId, cursor, limit);
    }

    // 댓글 단 영상 전체보기
    @GetMapping("/comments") 
    public ShelfPageResponse<VideoCard> getCommentedVideos(
        @RequestParam Long userId,
        @RequestParam(required=false) String cursor,
        @RequestParam(defaultValue="8") Integer limit
    ) {
        return shortsMypageService.getCommentedVideos(userId, cursor, limit);
    }

    // 구독 취소
    @DeleteMapping("/subscription")
    public ResponseEntity<Void> deleteSubscription(@RequestParam Long userId, @RequestParam Long targetId) {
        long result = shortsMypageService.deleteSubscription(userId, targetId);
        if(result > 0) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
}
