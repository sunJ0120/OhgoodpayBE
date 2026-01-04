package com.ohgoodteam.ohgoodpay.shorts.repository.search;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.search.ShortsSearchResponseDTO.ShortsSearchResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        BigDecimal lastScore,
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
                  (:wHashtag * (CASE WHEN s.shorts_explain RLIKE '#[0-9A-Za-z가-힣_]+' THEN 1 ELSE 0 END)) +
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
        
        
        Query query = entityManager.createNativeQuery(sql);
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
        
        @SuppressWarnings("unchecked")
        List<Object[]> rawResults = query.getResultList();
        
        List<ShortsSearchResponse> result = rawResults.stream()
                .map(row -> {
                    try {
                        // 밑에 전부 다 안전하게 타입 변환 하는 것
                        // 예로 들어, DB의 BIGINT가 long, Integer, BigInteger 등 다양한 타입으로 저장될 수 있기 때문에
                        Long shortsId = null;
                        if (row[0] != null) {
                            if (row[0] instanceof Number) { // Number 타입인 경우
                                shortsId = ((Number) row[0]).longValue(); // Long 타입으로 변환
                            } else {
                                shortsId = Long.parseLong(row[0].toString());
                            }
                        }
                        
                        String thumbnail = row[1] != null ? row[1].toString() : null;
                        
                        Long likeCount = null;
                        if (row[2] != null) {
                            if (row[2] instanceof Number) {
                                likeCount = ((Number) row[2]).longValue();
                            } else {
                                likeCount = Long.parseLong(row[2].toString());
                            }
                        }
                        
                        LocalDateTime date = null;
                        if (row[3] != null) {
                            if (row[3] instanceof java.sql.Timestamp) {
                                date = ((java.sql.Timestamp) row[3]).toLocalDateTime();
                            } else if (row[3] instanceof java.sql.Date) {
                                date = ((java.sql.Date) row[3]).toLocalDate().atStartOfDay();
                            } else {
                                // 문자열로 파싱 시도
                                date = java.time.LocalDateTime.parse(row[3].toString().replace(" ", "T"));
                            }
                        }
                        
                        BigDecimal score = null;
                        if (row[4] != null) {
                            if (row[4] instanceof BigDecimal) {
                                score = (BigDecimal) row[4];
                            } else if (row[4] instanceof Number) {
                                score = BigDecimal.valueOf(((Number) row[4]).doubleValue());
                            } else {
                                score = new BigDecimal(row[4].toString());
                            }
                        }
                        
                        return new ShortsSearchResponse(shortsId, thumbnail, likeCount, date, score);
                    } catch (Exception e) {
                        throw new RuntimeException("Row 매핑 실패", e);
                    }
                })
                .toList();
        
        return result;
    }
}
