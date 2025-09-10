package com.ohgoodteam.ohgoodpay.shorts.service.feed;

import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsCommentRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.ShortsCommentDataDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.ShortsFeedDataDto;

import java.util.List;

public interface ShortsFeedService {

    // 전체 쇼츠 피드 조회
    List<ShortsFeedDataDto> findAllFeeds(int page, int size, String keyword);

    // 댓글 조회
    List<ShortsCommentDataDto> findAllComments(Long shortsId);

    // 댓글 작성
    ShortsCommentDataDto createComment(Long shortsId, ShortsCommentRequestDto shortsCommentRequestDto);

}
