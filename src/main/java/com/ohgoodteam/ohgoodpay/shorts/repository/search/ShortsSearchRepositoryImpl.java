package com.ohgoodteam.ohgoodpay.shorts.repository.search;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.search.ShortsSearchResponseDto.ShortsSearchResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShortsSearchRepositoryImpl implements ShortsSearchRepositoryCustom {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    // 검색 결과 조회 (가중치 로직, 점수 계산, 정렬, 페이징 처리)
    @Override
    @SuppressWarnings("unchecked") // ShortsSearchResponse로 매핑해줄때 컴파일러 경고 무시
    public List<ShortsSearchResponse> fetchExposure(
        String q,
        double wLike,
        double wComment,
        double wHashtag,
        double wRecency,
        double tauHours,
        Double lastScore,
        LocalDateTime lastDate,
        Long lastId,
        int limitPlusOne
    ) {
        // 복잡한 가중치 로직은 native query로 유지
        String sql = """
            SELECT t.shorts_id   AS shortsId,
                   t.thumbnail   AS thumbnail,
                   t.like_count  AS likeCount,
                   t.date        AS date,
                   t.score       AS score
            FROM (
              SELECT 
                s.shorts_id,
                s.thumbnail,
                s.like_count,
                s.comment_count,
                s.date,
                CAST(
                  (:wLike    * LOG(1 + s.like_count)) +
                  (:wComment * LOG(1 + s.comment_count)) +
                  (:wHashtag * (CASE WHEN s.shorts_explain REGEXP '#[0-9A-Za-z가-힣_]+' THEN 1 ELSE 0 END)) +
                  (:wRecency * EXP(- GREATEST(TIMESTAMPDIFF(HOUR, s.date, NOW()),0) / :tauHours))
                AS DECIMAL(12,6)) AS score
              FROM shorts s
              WHERE (:q IS NULL OR s.shorts_explain LIKE CONCAT('%', :q, '%'))
            ) t
            WHERE (
              :lastScore IS NULL
              OR t.score < :lastScore
              OR (t.score = :lastScore AND t.date < :lastDate)
              OR (t.score = :lastScore AND t.date = :lastDate AND t.shorts_id < :lastId)
            )
            ORDER BY t.score DESC, t.date DESC, t.shorts_id DESC
            LIMIT :limitPlusOne
            """;
        
        Query query = entityManager.createNativeQuery(sql, ShortsSearchResponse.class);
        query.setParameter("q", q);
        query.setParameter("wLike", wLike);
        query.setParameter("wComment", wComment);
        query.setParameter("wHashtag", wHashtag);
        query.setParameter("wRecency", wRecency);
        query.setParameter("tauHours", tauHours);
        query.setParameter("lastScore", lastScore);
        query.setParameter("lastDate", lastDate);
        query.setParameter("lastId", lastId);
        query.setParameter("limitPlusOne", limitPlusOne);
        
        return query.getResultList();
    }
}
