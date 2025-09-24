package com.ohgoodteam.ohgoodpay.shorts.controller.mypage;

import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDTO.ShelfPageResponse;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDTO.UserCard;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDTO.VideoCard;
import com.ohgoodteam.ohgoodpay.shorts.service.mypage.ShortsMypageService;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ShortsMypageController {

    private final ShortsMypageService shortsMypageService;

    /**
     * 마이페이지 미리보기
     * @param userId
     * @param limit
     * @return
     */
    @GetMapping("/shorts/mypage/{userId}/overview")
    public ShortsMypageResponseDTO getOverview(@PathVariable Long userId, @RequestParam(defaultValue="8") Integer limit) {
        return shortsMypageService.getOverview(userId, limit);
    }

    /**
     * 구독 전체보기
     * @param userId
     * @param cursor
     * @param limit
     * @return
     */
    @GetMapping("/shorts/mypage/subscribe")
    public ShelfPageResponse<UserCard> getSubscription(
        @RequestParam Long userId,
        @RequestParam(required=false) String cursor,
        @RequestParam(defaultValue="8") Integer limit
    ) {
        return shortsMypageService.getSubscriptions(userId, cursor, limit);
    }

    /**
     * 좋아요 한 영상 전체보기
     * @param userId
     * @param cursor
     * @param limit
     * @return
     */
    @GetMapping("/shorts/mypage/all")
    public ShelfPageResponse<VideoCard> getLikedVideos(
        @RequestParam Long userId,
        @RequestParam(required=false) String cursor,
        @RequestParam(defaultValue="8") Integer limit
    ) {
        return shortsMypageService.getLikedVideos(userId, cursor, limit);
    }

    /**
     * 댓글 단 영상 전체보기
     * @param userId
     * @param cursor
     * @param limit
     * @return
     */
    @GetMapping("/shorts/mypage/comments")
    public ShelfPageResponse<VideoCard> getCommentedVideos(
        @RequestParam Long userId,
        @RequestParam(required=false) String cursor,
        @RequestParam(defaultValue="8") Integer limit
    ) {
        return shortsMypageService.getCommentedVideos(userId, cursor, limit);
    }

    /**
     * 구독 취소
     * @param userId
     * @param targetId
     * @return
     */
    @DeleteMapping("/shorts/mypage/subscription")
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
