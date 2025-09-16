package com.ohgoodteam.ohgoodpay.shorts.service.mypage;

import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDto.ShelfPageResponse;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDto.UserCard;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDto.VideoCard;

public interface ShortsMypageService {
    // 구독, 좋아요 한 영상, 댓글 단 영상 미리보기
    ShortsMypageResponseDto getOverview(Long userId, int limit);

    // 구독 전체보기
    ShelfPageResponse<UserCard> getSubscriptions(Long userId, String cursor, int limit);

    // 좋아요 한 영상 전체보기
    ShelfPageResponse<VideoCard> getLikedVideos(Long userId, String cursor, int limit);

    // 댓글 단 영상 전체보기
    ShelfPageResponse<VideoCard> getCommentedVideos(Long userId, String cursor, int limit);

    // 구독 취소
    long deleteSubscription(Long userId, Long targetId);
}
