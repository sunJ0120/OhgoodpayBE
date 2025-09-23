package com.ohgoodteam.ohgoodpay.shorts.repository.feed;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.ohgoodteam.ohgoodpay.common.entity.QCommentEntity;
import com.ohgoodteam.ohgoodpay.common.entity.QCustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.QReactionEntity;
import com.ohgoodteam.ohgoodpay.common.entity.QShortsEntity;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import com.ohgoodteam.ohgoodpay.shorts.repository.ReactionRepository.VideoJoinRow;

@Repository
@RequiredArgsConstructor
public class ReactionRepositoryImpl implements ReactionRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    private final QReactionEntity reaction = QReactionEntity.reactionEntity;
    private final QShortsEntity shorts = QShortsEntity.shortsEntity;
    private final QCustomerEntity customer = QCustomerEntity.customerEntity;
    private final QCommentEntity comment = QCommentEntity.commentEntity;
    
    // 좋아요한 쇼츠 미리보기 조회
    @Override
    public List<VideoJoinRow> findLikedShortsPreview(Long meId, int size) {
        return queryFactory
            .select(Projections.bean(VideoJoinRow.class,
                reaction.reactionId.as("cursorId"),
                shorts.shortsId,
                shorts.shortsName,
                shorts.shortsExplain,
                shorts.thumbnail,
                shorts.videoName,
                shorts.likeCount,
                shorts.commentCount,
                shorts.date,
                customer.customerId.as("ownerId"),
                customer.name.as("ownerName"),
                customer.nickname.as("ownerNickname"),
                customer.profileImg.as("ownerProfileImg")
            ))
            .from(reaction)
            .join(shorts).on(shorts.shortsId.eq(reaction.shorts.shortsId))
            .join(customer).on(customer.customerId.eq(shorts.customer.customerId))
            .where(reaction.customer.customerId.eq(meId)
                .and(reaction.react.eq("LIKE")))
            .orderBy(reaction.reactionId.desc())
            .limit(size)
            .fetch();
    }
    
    // 좋아요한 영상 전체보기
    @Override
    public List<VideoJoinRow> findLikedShortsPage(Long meId, Long lastReactionId, int size) {
        return queryFactory
            .select(Projections.bean(VideoJoinRow.class,
                reaction.reactionId.as("cursorId"),
                shorts.shortsId,
                shorts.shortsName,
                shorts.shortsExplain,
                shorts.thumbnail,
                shorts.videoName,
                shorts.likeCount,
                shorts.commentCount,
                shorts.date,
                customer.customerId.as("ownerId"),
                customer.name.as("ownerName"),
                customer.nickname.as("ownerNickname"),
                customer.profileImg.as("ownerProfileImg")
            ))
            .from(reaction)
            .join(shorts).on(shorts.shortsId.eq(reaction.shorts.shortsId))
            .join(customer).on(customer.customerId.eq(shorts.customer.customerId))
            .where(reaction.customer.customerId.eq(meId)
                .and(reaction.react.eq("LIKE"))
                .and(lastReactionId == null ? null : reaction.reactionId.lt(lastReactionId)))
            .orderBy(reaction.reactionId.desc())
            .limit(size)
            .fetch();
    }
    
    // 댓글 단 쇼츠 미리보기 조회
    @Override
    public List<VideoJoinRow> findCommentedShortsPreview(Long meId, int size) {
        return queryFactory
            .select(Projections.bean(VideoJoinRow.class,
                comment.commentId.as("cursorId"),
                comment.content.as("context"),
                shorts.shortsId,
                shorts.shortsName,
                shorts.shortsExplain,
                shorts.thumbnail,
                shorts.videoName,
                shorts.likeCount,
                shorts.commentCount,
                shorts.date,
                customer.customerId.as("ownerId"),
                customer.name.as("ownerName"),
                customer.nickname.as("ownerNickname"),
                customer.profileImg.as("ownerProfileImg")
            ))
            .from(comment)
            .join(shorts).on(shorts.shortsId.eq(comment.shorts.shortsId))
            .join(customer).on(customer.customerId.eq(shorts.customer.customerId))
            .where(comment.customer.customerId.eq(meId))
            .orderBy(comment.commentId.desc())
            .limit(size)
            .fetch();
    }
    
    // 댓글 단 쇼츠 전체보기
    @Override
    public List<VideoJoinRow> findCommentedShortsPage(Long meId, Long lastReactionId, int size) {
        return queryFactory
            .select(Projections.bean(VideoJoinRow.class,
                comment.commentId.as("cursorId"),
                shorts.shortsId,
                shorts.shortsName,
                shorts.shortsExplain,
                shorts.thumbnail,
                shorts.videoName,
                shorts.likeCount,
                shorts.commentCount,
                shorts.date,
                customer.customerId.as("ownerId"),
                customer.name.as("ownerName"),
                customer.nickname.as("ownerNickname"),
                customer.profileImg.as("ownerProfileImg"),
                comment.content.as("context")
            ))
            .from(comment)
            .join(shorts).on(shorts.shortsId.eq(comment.shorts.shortsId))
            .join(customer).on(customer.customerId.eq(shorts.customer.customerId))
            .where(comment.customer.customerId.eq(meId)
                .and(lastReactionId == null ? null : comment.commentId.lt(lastReactionId)))
            .orderBy(comment.commentId.desc())
            .limit(size)
            .fetch();
    }
}
