package com.ohgoodteam.ohgoodpay.shorts.repository.feed;

import com.ohgoodteam.ohgoodpay.shorts.repository.ReactionRepository.VideoJoinRow;
import java.util.List;


public interface ReactionRepositoryCustom {
    
    /**
     * 좋아요한 쇼츠 미리보기 조회
     */
    List<VideoJoinRow> findLikedShortsPreview(Long meId, int size);
    
    /**
     * 좋아요한 쇼츠 페이지네이션 조회
     */
    List<VideoJoinRow> findLikedShortsPage(Long meId, Long lastReactionId, int size);
    
    /**
     * 댓글 단 쇼츠 미리보기 조회
     */
    List<VideoJoinRow> findCommentedShortsPreview(Long meId, int size);
    
    /**
     * 댓글 단 쇼츠 페이지네이션 조회
     */
    List<VideoJoinRow> findCommentedShortsPage(Long meId, Long lastReactionId, int size);
}
