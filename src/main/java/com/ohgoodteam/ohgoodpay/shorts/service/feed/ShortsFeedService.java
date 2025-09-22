package com.ohgoodteam.ohgoodpay.shorts.service.feed;

import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsCommentRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsPointEarnRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsPointRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsReactionRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.*;
import java.util.List;

public interface ShortsFeedService {

    // 전체 쇼츠 피드 조회
    List<ShortsFeedDataDto> findAllFeeds(int page, int size, String keyword, Long customerId);

    // 전체 쇼츠 피드 조회 (v2)
    ShortsFeedListDataDto findAllFeedsV2(int page, int size, String keyword);

    // 댓글 조회
    List<ShortsCommentDataDto> findAllComments(Long shortsId);

    // 댓글 조회 (v2)
    ShortsCommentListDataDto findAllCommentsV2(Long shortsId);

    // 댓글 작성
    boolean createComment(Long shortsId, ShortsCommentRequestDto shortsCommentRequestDto);

    // 댓글 작성 (v2)
    ShortsCommentDataDto createCommentV2(Long shortsId, ShortsCommentRequestDto shortsCommentRequestDto);

    // 좋아요 반응 처리
    ShortsReactionDataDto reactToShorts(ShortsReactionRequestDto shortsReactionRequestDto);

    // // 포인트 적립 가능한지 조회
    // ShortsPointResponseDto getPointStatus(Long customerId);

    // // 쇼츠 시청 포인트 적립
    // ShortsPointResponseDto watchFeed(Long customerId, ShortsPointRequestDto requestDto);

    // 공유기능 -> 특정 영상에 대한 정보 반환
    ShortsFeedDataDto getSpecificShorts(Long shortsId);

    // 포인트 게이지 포인트 적립
    ShortsPointEarnResponseDto earnPoint(ShortsPointEarnRequestDto requestDto);

    // 댓글 삭제
    ShortsCommentDeleteDataDto deleteComment(Long shortsId, Long commentId, Long customerId);
}
