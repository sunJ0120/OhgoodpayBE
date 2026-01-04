package com.ohgoodteam.ohgoodpay.shorts.service.mypage;

import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDTO;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDTO.ShelfPageResponse;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDTO.UserCard;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDTO.VideoCard;

public interface ShortsMypageService {
    // 구독, 좋아요 한 영상, 댓글 단 영상 미리보기
    ShortsMypageResponseDTO getOverview(Long userId, int limit);

    // 구독 전체보기
    ShelfPageResponse<UserCard> getSubscriptions(Long userId, String cursor, int limit);

    // 좋아요 한 영상 전체보기
    ShelfPageResponse<VideoCard> getLikedVideos(Long userId, String cursor, int limit);

    // 댓글 단 영상 전체보기
    ShelfPageResponse<VideoCard> getCommentedVideos(Long userId, String cursor, int limit);

    // 구독 취소
    long deleteSubscription(Long userId, Long targetId);
}
