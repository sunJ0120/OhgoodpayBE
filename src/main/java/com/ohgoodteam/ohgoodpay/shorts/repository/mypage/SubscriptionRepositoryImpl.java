package com.ohgoodteam.ohgoodpay.shorts.repository.mypage;

import java.util.List;

import com.ohgoodteam.ohgoodpay.shorts.repository.SubscriptionRepository;
import org.springframework.stereotype.Repository;
import com.ohgoodteam.ohgoodpay.common.entity.QCustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.QSubscriptionEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    private final QSubscriptionEntity subscription = QSubscriptionEntity.subscriptionEntity;
    private final QCustomerEntity customer = QCustomerEntity.customerEntity;
    
    // 구독 목록 전체보기
    @Override
    public List<SubscriptionRepository.FollowingRow> findFollowingPage(Long meId, Long lastSubscriptionId, int size) {
        return queryFactory
            .select(Projections.bean(SubscriptionRepository.FollowingRow.class,
                subscription.subscriptionId.as("cursorId"),
                customer.customerId.as("followingId"),
                customer.name.as("name"),
                customer.nickname.as("nickname"),
                customer.profileImg.as("profileImg")
            ))
            .from(subscription)
            .join(customer).on(customer.customerId.eq(subscription.following.customerId))
            .where(subscription.follower.customerId.eq(meId)
                .and(lastSubscriptionId == null ? null : subscription.subscriptionId.lt(lastSubscriptionId)))
            .orderBy(subscription.subscriptionId.desc())
            .limit(size)
            .fetch();
    }
}
