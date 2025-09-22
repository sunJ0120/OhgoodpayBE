package com.ohgoodteam.ohgoodpay.shorts.repository;

import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.ShortsCommonResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShortsRepository extends JpaRepository<ShortsEntity, Long> {

    /**
     * 전체 쇼츠 피드 조회 (가중치 적용)
     * @param wLike
     * @param wComment
     * @param wHashtag
     * @param wRecency
     * @param tauHours
     * @param customerId
     * @param pageable
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
            r.react as myReaction,
            CAST(
              (:wLike    * LOG(1 + s.like_count)) +
              (:wComment * LOG(1 + s.comment_count)) +
              (:wHashtag * (CASE WHEN s.shorts_explain REGEXP '#[0-9A-Za-z가-힣_]+' THEN 1 ELSE 0 END)) +
              (:wRecency * EXP(- GREATEST(TIMESTAMPDIFF(HOUR, s.date, NOW()),0) / :tauHours))
            AS DECIMAL(12,6)) AS score
        FROM shorts s
        LEFT JOIN customer c ON s.customer_id = c.customer_id
        LEFT JOIN reaction r ON s.shorts_id = r.shorts_id AND r.customer_id = :customerId
        ORDER BY score DESC, s.date DESC, s.shorts_id DESC
    """, nativeQuery = true)
    Page<Object[]> findAllFeeds(@Param("wLike") double wLike,
                                 @Param("wComment") double wComment,
                                @Param("wHashtag") double wHashtag,
                                @Param("wRecency") double wRecency,
                                @Param("tauHours") double tauHours,
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


    // point_history 테이블에 포인트 적립 내역 저장
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO point_history (customer_id, point, point_explain, date)
        VALUES (:customerId, :points, :reason, :date)
        """, nativeQuery = true)
    void insertPointHistory(@Param("customerId") Long customerId,
                            @Param("points") int points,
                            @Param("reason") String reason,
                            @Param("date") LocalDateTime date);


    // 오늘 적립된 포인트 합계 내역 조회
    @Query(value = """
    SELECT COALESCE(SUM(ph.point), 0)
    FROM point_history ph
    WHERE ph.customer_id = :customerId
      AND ph.point_explain = :reason
      AND ph.date >= :start
      AND ph.date < :end
    """, nativeQuery = true)
    int sumTodayPoints(Long customerId, String reason, LocalDateTime start, LocalDateTime end);

    /**
     * 커서 기반 페이징을 사용한 전체 쇼츠 피드 조회 (가중치 적용)
     * @param wLike 좋아요 가중치
     * @param wComment 댓글 가중치
     * @param wHashtag 해시태그 가중치
     * @param wRecency 최신성 가중치
     * @param tauHours 시간 상수
     * @param customerId 고객 ID
     * @param lastScore 마지막 점수 (커서)
     * @param lastDate 마지막 날짜 (커서)
     * @param lastId 마지막 ID (커서)
     * @param limitPlusOne 조회할 개수 + 1
     * @return Object[] 배열 리스트
     */
    @Query(value = """
        SELECT 
            t.shorts_id,
            t.video_name,
            t.thumbnail,
            t.shorts_name,
            t.shorts_explain,
            t.date,
            c.customer_id,
            c.nickname,
            c.profile_img,
            t.like_count,
            t.comment_count,
            r.react as myReaction,
            t.score
        FROM (
          SELECT 
            s.shorts_id,
            s.video_name,
            s.thumbnail,
            s.shorts_name,
            s.shorts_explain,
            s.date,
            s.customer_id,
            s.like_count,
            s.comment_count,
            CAST(
              (:wLike    * LOG(1 + s.like_count)) +
              (:wComment * LOG(1 + s.comment_count)) +
              (:wHashtag * (CASE WHEN s.shorts_explain REGEXP '#[0-9A-Za-z가-힣_]+' THEN 1 ELSE 0 END)) +
              (:wRecency * EXP(- GREATEST(TIMESTAMPDIFF(HOUR, s.date, NOW()),0) / :tauHours))
            AS DECIMAL(12,6)) AS score
          FROM shorts s
        ) t
        LEFT JOIN customer c ON t.customer_id = c.customer_id
        LEFT JOIN reaction r ON t.shorts_id = r.shorts_id AND r.customer_id = :customerId
        WHERE (
          :lastScore IS NULL
          OR t.score < :lastScore
          OR (t.score = :lastScore AND t.date < :lastDate)
          OR (t.score = :lastScore AND t.date = :lastDate AND t.shorts_id < :lastId)
        )
        ORDER BY t.score DESC, t.date DESC, t.shorts_id DESC
        LIMIT :limitPlusOne
    """, nativeQuery = true)
    List<Object[]> findAllFeedsWithCursor(@Param("wLike") double wLike,
                                         @Param("wComment") double wComment,
                                         @Param("wHashtag") double wHashtag,
                                         @Param("wRecency") double wRecency,
                                         @Param("tauHours") double tauHours,
                                         @Param("customerId") Long customerId,
                                         @Param("lastScore") Double lastScore,
                                         @Param("lastDate") LocalDateTime lastDate,
                                         @Param("lastId") Long lastId,
                                         @Param("limitPlusOne") int limitPlusOne);


    /**
     * 특정 고객이 작성한 쇼츠 수 조회
     * @param customerId
     * @return
     */
    @Query("""
        SELECT COUNT(s)
        FROM ShortsEntity s
        WHERE s.customer.customerId = :customerId
    """)
    Long countShorts(@Param("customerId") Long customerId);

    /**
     * 특정 고객의 쇼츠 프로필 데이터 조회 (커서 기반 페이징)
     * @param customerId
     * @param pageable
     * @return
     */
    @Query("""
    SELECT s.shortsId, s.likeCount, s.thumbnail
    FROM ShortsEntity s
    WHERE s.customer.customerId = :customerId
    """)
    Page<Object[]> findShortsProfileDataByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
}
