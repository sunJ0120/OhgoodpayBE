package com.ohgoodteam.ohgoodpay.shorts.repository;

import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.ShortsCommonResponse;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShortsRepository extends JpaRepository<ShortsEntity, Long> {

    @Query("""
        SELECT s FROM ShortsEntity s
        LEFT JOIN FETCH s.customer c
        WHERE s.shortsName LIKE %:keyword% OR s.shortsExplain LIKE %:keyword%
    """)
    List<ShortsEntity> findAllFeeds(@Param("keyword") String keyword, Pageable pageable);



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



    @Query(value = """
        SELECT COALESCE(SUM(ph.point), 0)
          FROM point_history ph
         WHERE ph.customer_id = :customerId
           AND ph.point_explain = :reason
           AND ph.date >= :start
           AND ph.date <  :end
        """, nativeQuery = true)
    int sumTodayShortsPoint(Long customerId, String reason, LocalDateTime start, LocalDateTime end);


    @Query(value = "SELECT customer_id FROM customer WHERE customer_id = :customerId FOR UPDATE", nativeQuery = true)
    Long lockCustomerRow(Long customerId);


    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO point_history (customer_id, point, point_explain, date)
        VALUES (:customerId, :pointPerLap, :reason, :kstNow)
        """, nativeQuery = true)
    void insertPointHistory(Long customerId, int pointPerLap, String reason, LocalDateTime kstNow);


    @Modifying
    @Transactional
    @Query(value = """
        UPDATE customer
           SET point = point + :pointPerLap
         WHERE customer_id = :customerId
        """, nativeQuery = true)
    void addCustomerPoint(Long customerId, int pointPerLap);

}
