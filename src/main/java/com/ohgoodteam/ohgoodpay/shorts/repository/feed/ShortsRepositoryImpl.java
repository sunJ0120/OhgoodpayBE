package com.ohgoodteam.ohgoodpay.shorts.repository.feed;

import java.time.LocalDateTime;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.ohgoodteam.ohgoodpay.common.entity.QPointHistoryEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShortsRepositoryImpl implements ShortsRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    private final QPointHistoryEntity pointHistory = QPointHistoryEntity.pointHistoryEntity;
    
    // 포인트 적립 기록
    @Override
    @Transactional
    public void insertPointHistory(Long customerId, int points, String reason, LocalDateTime date) {
        queryFactory
            .insert(pointHistory)
            .columns(
                pointHistory.customer.customerId,
                pointHistory.point,
                pointHistory.pointExplain,
                pointHistory.date
            )
            .values(
                customerId,
                points,
                reason,
                date
            )
            .execute();
    }
    
    // 오늘 적립된 포인트 조회
    @Override
    public int sumTodayPoints(Long customerId, String reason, LocalDateTime start, LocalDateTime end) {
        Integer result = queryFactory
            .select(pointHistory.point.sum())
            .from(pointHistory)
            .where(pointHistory.customer.customerId.eq(customerId)
                .and(pointHistory.pointExplain.eq(reason))
                .and(pointHistory.date.goe(start))
                .and(pointHistory.date.lt(end)))
            .fetchOne();
        
        return result != null ? result : 0;
    }
}
