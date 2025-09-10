package com.ohgoodteam.ohgoodpay.shorts.repository;

import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.ShortsCommonResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

}
