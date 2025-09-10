package com.ohgoodteam.ohgoodpay.shorts.repository.search;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.search.ShortsSearchResponseDto.ShortsSearchResponse;

public interface ShortsSearchRepository extends JpaRepository<ShortsEntity, Long> {
    @Query(value = """
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
    """, nativeQuery = true)
  List<ShortsSearchResponse> fetchExposure(
      @Param("q") String q,
      @Param("wLike") double wLike,
      @Param("wComment") double wComment,
      @Param("wHashtag") double wHashtag,
      @Param("wRecency") double wRecency,
      @Param("tauHours") double tauHours,
      @Param("lastScore") Double lastScore,
      @Param("lastDate") LocalDateTime lastDate,
      @Param("lastId") Long lastId,
      @Param("limitPlusOne") int limitPlusOne
  );
}
