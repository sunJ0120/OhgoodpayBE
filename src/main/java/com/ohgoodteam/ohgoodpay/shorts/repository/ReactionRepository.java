package com.ohgoodteam.ohgoodpay.shorts.repository;

import java.util.List;
import java.util.Optional;

import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ohgoodteam.ohgoodpay.common.entity.ReactionEntity;
import org.springframework.transaction.annotation.Transactional;

public interface ReactionRepository extends JpaRepository<ReactionEntity, Long> {

    // 좋아요 미리보기
    @Query(value = """
        SELECT
          r.reaction_id AS cursorId,
          s.shorts_id AS shortsId,
          s.shorts_name AS shortsName,
          s.shorts_explain AS shortsExplain,
          s.thumbnail AS thumbnail,
          s.video_name AS videoName,
          s.like_count AS likeCount,
          s.comment_count AS commentCount,
          s.date AS date,
          c.customer_id AS ownerId,
          c.name AS ownerName,
          c.nickname AS ownerNickname,
          c.profile_img AS ownerProfileImg
        FROM reaction r
        JOIN shorts s ON s.shorts_id = r.shorts_id
        JOIN customer c ON c.customer_id = s.customer_id
        WHERE r.customer_id = :meId AND r.react = 'LIKE'
        ORDER BY r.reaction_id DESC
        LIMIT :size
    """, nativeQuery = true)
    List<VideoJoinRow> findLikedShortsPreview(@Param("meId") Long meId, @Param("size") int size);

    // 좋아요 페이지
    @Query(value = """
        SELECT
          r.reaction_id AS cursorId,
          s.shorts_id AS shortsId,
          s.shorts_name AS shortsName,
          s.shorts_explain AS shortsExplain,
          s.thumbnail AS thumbnail,
          s.video_name AS videoName,
          s.like_count AS likeCount,
          s.comment_count AS commentCount,
          s.date AS date,
          c.customer_id AS ownerId,
          c.name AS ownerName,
          c.nickname AS ownerNickname,
          c.profile_img AS ownerProfileImg
        FROM reaction r
        JOIN shorts s ON s.shorts_id = r.shorts_id
        JOIN customer c ON c.customer_id = s.customer_id
        WHERE r.customer_id = :meId 
          AND r.react = 'LIKE'
          AND (:lastId IS NULL OR r.reaction_id < :lastId)
        ORDER BY r.reaction_id DESC
        LIMIT :size
    """, nativeQuery = true)
    List<VideoJoinRow> findLikedShortsPage(@Param("meId") Long meId, @Param("lastId") Long lastReactionId, @Param("size") int size);

    // 댓글 미리보기
    @Query(value = """
        SELECT
          cm.comment_id AS cursorId,
          cm.content AS context,
          s.shorts_id AS shortsId,
          s.shorts_name AS shortsName,
          s.shorts_explain AS shortsExplain,
          s.thumbnail AS thumbnail,
          s.video_name AS videoName,
          s.like_count AS likeCount,
          s.comment_count AS commentCount,
          s.date AS date,
          c.customer_id AS ownerId,
          c.name AS ownerName,
          c.nickname AS ownerNickname,
          c.profile_img AS ownerProfileImg
        FROM comment cm
        JOIN shorts s ON s.shorts_id = cm.shorts_id
        JOIN customer c ON c.customer_id = s.customer_id
        WHERE cm.customer_id = :meId
        ORDER BY cm.comment_id DESC
        LIMIT :size
    """, nativeQuery = true)
    List<VideoJoinRow> findCommentedShortsPreview(@Param("meId") Long meId, @Param("size") int size);

    // 댓글 페이지
    @Query(value = """
        SELECT
          cm.comment_id AS cursorId,
          s.shorts_id AS shortsId,
          s.shorts_name AS shortsName,
          s.shorts_explain AS shortsExplain,
          s.thumbnail AS thumbnail,
          s.video_name AS videoName,
          s.like_count AS likeCount,
          s.comment_count AS commentCount,
          s.date AS date,
          c.customer_id AS ownerId,
          c.name AS ownerName,
          c.nickname AS ownerNickname,
          c.profile_img AS ownerProfileImg,
          cm.content AS context
        FROM comment cm
        JOIN shorts s ON s.shorts_id = cm.shorts_id
        JOIN customer c ON c.customer_id = s.customer_id
        WHERE cm.customer_id = :meId 
          AND (:lastId IS NULL OR cm.comment_id < :lastId)
        ORDER BY cm.comment_id DESC
        LIMIT :size
    """, nativeQuery = true)
    List<VideoJoinRow> findCommentedShortsPage(@Param("meId") Long meId, @Param("lastId") Long lastReactionId, @Param("size") int size);


    // Projection
    interface VideoJoinRow {
        Long getCursorId();          
        String getContext();
        Long getShortsId();
        String getShortsName();
        String getShortsExplain();
        String getThumbnail();
        String getVideoName();
        Long getLikeCount();
        Long getCommentCount();
        java.time.LocalDateTime getDate();
        Long getOwnerId();
        String getOwnerName();
        String getOwnerNickname();
        String getOwnerProfileImg();
    }


    /**
     * 특정 회원이 특정 쇼츠에 특정 반응을 했는지 여부 확인
     * @param customer
     * @param shorts
     * @param type
     * @return
     */
    boolean existsByCustomerAndShortsAndReact(CustomerEntity customer, ShortsEntity shorts, String type);


    /**
     *
     * @param customer
     * @param shorts
     * @return
     */
    Optional<ReactionEntity> findByCustomerAndShorts(CustomerEntity customer, ShortsEntity shorts);

    @Modifying
    @Transactional
    @Query("""
    UPDATE ReactionEntity r
    SET r.react = :newReact
    WHERE r.customer = :customer AND r.shorts = :shorts
    """)
    int updateReaction(@Param("customer") CustomerEntity customer,
                       @Param("shorts") ShortsEntity shorts,
                       @Param("newReact") String newReact);
}
