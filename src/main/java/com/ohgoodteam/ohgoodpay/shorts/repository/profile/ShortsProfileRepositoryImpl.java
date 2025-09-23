package com.ohgoodteam.ohgoodpay.shorts.repository.profile;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.ohgoodteam.ohgoodpay.common.entity.QCustomerEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ShortsProfileRepositoryImpl implements ShortsProfileRepositoryCustom {
    
    private final JPAQueryFactory queryFactory;
    
    private final QCustomerEntity customer = QCustomerEntity.customerEntity;
    
    // 프로필 수정
    @Override
    @Transactional
    public int updateProfile(Long customerId, String nickname, String introduce, String profileImg) {
        return (int) queryFactory
            .update(customer)
            .set(customer.nickname, nickname)
            .set(customer.introduce, introduce)
            .set(customer.profileImg, profileImg)
            .where(customer.customerId.eq(customerId))
            .execute();
    }
}
