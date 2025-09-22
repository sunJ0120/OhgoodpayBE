package com.ohgoodteam.ohgoodpay.shorts.repository;

import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.ShortsCommonResponse;
import com.ohgoodteam.ohgoodpay.shorts.repository.feed.ShortsRepositoryCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface ShortsRepository extends JpaRepository<ShortsEntity, Long>, ShortsRepositoryCustom {

    /**
     * 전체 쇼츠 피드 조회
     * @param keyword
     * @param customerId
     * @param offset
     * @param size
     * @return
     */
//    @Query("""
//        SELECT s,
//               (SELECT r.react FROM ReactionEntity r WHERE r.shorts.shortsId = s.shortsId AND r.customer.customerId = :customerId ) AS myReaction
//        FROM ShortsEntity s
//        LEFT JOIN FETCH s.customer c
//        WHERE s.shortsName LIKE CONCAT('%', :keyword, '%')\s
//           OR s.shortsExplain LIKE CONCAT('%', :keyword, '%')
//    """)
//    List<ShortsFeedDataDto> findAllFeeds(@Param("keyword") String keyword, @Param("customerId") Long customerId, Pageable pageable);
    @Query(value = """
        SELECT 
            s.shorts_id,
            s.video_name,
            s.thumbnail,
            s.shorts_name,
            s.shorts_explain,
            s.date,
            c.customer_id,
            c.nickname,
            c.profile_img,
            s.like_count,
            s.comment_count,
            r.react as myReaction
        FROM shorts s
        LEFT JOIN customer c ON s.customer_id = c.customer_id
        LEFT JOIN reaction r ON s.shorts_id = r.shorts_id AND r.customer_id = :customerId
        WHERE (:keyword IS NULL
               OR s.shorts_name LIKE CONCAT('%', :keyword, '%')
               OR s.shorts_explain LIKE CONCAT('%', :keyword, '%'))
    """, nativeQuery = true)
    Page<Object[]> findAllFeeds(@Param("keyword") String keyword,
                               @Param("customerId") Long customerId,
                               Pageable pageable);


    /**
     * 전체 쇼츠 피드 조회 v2 (Page 객체 반환)
     * @param keyword
     * @param pageable
     * @return
     */
    @Query("""
        SELECT s FROM ShortsEntity s
        LEFT JOIN FETCH s.customer c
        WHERE s.shortsName LIKE CONCAT('%', :keyword, '%')\s
           OR s.shortsExplain LIKE CONCAT('%', :keyword, '%')
    """)
    Page<ShortsEntity> findAllFeedsV2(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 현재 쇼츠 댓글 수 증가
     */
    @Modifying
    @Transactional
    @Query("""
        UPDATE ShortsEntity s
        SET s.commentCount = s.commentCount + 1
        WHERE s.shortsId = :shortsId
        """)
    int incrementCommentCount(@Param("shortsId") Long shortsId);


    /**
     * 현재 쇼츠 좋아요 수 + 1
     */
    @Modifying
    @Transactional
    @Query("""
    UPDATE ShortsEntity s
    SET s.likeCount = s.likeCount + 1
    WHERE s.shortsId = :shortsId
    """)
    int incrementLikeCount(@Param("shortsId") Long shortsId);


    /**
     * 현재 쇼츠 좋아요 수 - 1
     */
    @Modifying
    @Transactional
    @Query("""
    UPDATE ShortsEntity s
    SET s.likeCount = s.likeCount - 1
    WHERE s.shortsId = :shortsId
    """)
    int decrementLikeCount(@Param("shortsId")Long shortsId);




    /**
     * 특정 고객의 쇼츠 피드 조회
     */
    @Query(value = """
        SELECT 
            s.shorts_id as shortsId,
            s.video_name as videoName,
            s.thumbnail as thumbnail,
            s.shorts_name as shortsName,
            s.shorts_explain as shortsExplain,
            s.date as date,
            c.customer_id as customerId,
            c.nickname as customerNickname,
            c.profile_img as profileImg,
            COALESCE(like_stats.like_count, 0) as likeCount,
            COALESCE(comment_stats.comment_count, 0) as commentCount
        FROM shorts s
        LEFT JOIN customer c ON s.customer_id = c.customer_id
        LEFT JOIN (
            SELECT 
                shorts_id,
                COUNT(*) as like_count
            FROM reaction 
            WHERE react = 'LIKE'
            GROUP BY shorts_id
        ) like_stats ON s.shorts_id = like_stats.shorts_id
        LEFT JOIN (
            SELECT 
                shorts_id,
                COUNT(*) as comment_count
            FROM comment 
            GROUP BY shorts_id
        ) comment_stats ON s.shorts_id = comment_stats.shorts_id
        WHERE s.customer_id = :customerId
        ORDER BY s.date DESC
        """, nativeQuery = true)
    List<ShortsCommonResponse> findShortsFeedByCustomerId(@Param("customerId") Long customerId);

    // @Query(value = """
    //     SELECT COALESCE(SUM(ph.point), 0)
    //       FROM point_history ph
    //      WHERE ph.customer_id = :customerId
    //        AND ph.point_explain = :reason
    //        AND ph.date >= :start
    //        AND ph.date <  :end
    //     """, nativeQuery = true)
    // int sumTodayShortsPoint(Long customerId, String reason, LocalDateTime start, LocalDateTime end);


    // @Query(value = "SELECT customer_id FROM customer WHERE customer_id = :customerId FOR UPDATE", nativeQuery = true)
    // Long lockCustomerRow(Long customerId);


    @Modifying
    @Transactional
    @Query(value = """
        UPDATE customer
           SET point = point + :pointPerLap
         WHERE customer_id = :customerId
        """, nativeQuery = true)
    void addCustomerPoint(Long customerId, int pointPerLap);
    /**
     * 특정 쇼츠의 좋아요 수
     * @param shortsId
     * @return
     */
    @Query("""
        SELECT s.likeCount
        FROM ShortsEntity s
        WHERE s.shortsId = :shortsId
    """)
    int findLikeCountById(@Param("shortsId") Long shortsId);


    // 사용자 포인트 업데이트
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE customer
        SET point = point + :points
        WHERE customer_id = :customerId
        """, nativeQuery = true)
    void updateCustomerPoints(Long customerId, int points);
}
