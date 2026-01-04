package com.ohgoodteam.ohgoodpay.shorts.repository.feed;

import java.time.LocalDateTime;

public interface ShortsRepositoryCustom {
    
    // point_history 테이블에 포인트 적립 기록
    void insertPointHistory(Long customerId, int points, String reason, LocalDateTime date);
    
    // 오늘 적립된 포인트 합계 조회
    int sumTodayPoints(Long customerId, String reason, LocalDateTime start, LocalDateTime end);
}
