package com.ohgoodteam.ohgoodpay.shorts.repository.profile;

import com.ohgoodteam.ohgoodpay.common.entity.QCustomerEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ShortsProfileRepositoryImpl implements ShortsProfileRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QCustomerEntity customer = QCustomerEntity.customerEntity;
    // 프로필 수정 (이미지 포함)
    @Override
    @Transactional
    public int updateProfileWithImage(Long customerId, String nickname, String introduce, String profileImg) {
        return (int) queryFactory
                .update(customer)
                .set(customer.nickname, nickname)
                .set(customer.introduce, introduce)
                .set(customer.profileImg, profileImg)
                .where(customer.customerId.eq(customerId))
                .execute();
    }
    // 프로필 수정 (이미지 제외)
    @Override
    @Transactional
    public int updateProfileWithoutImage(Long customerId, String nickname, String introduce) {
        return (int) queryFactory
                .update(customer)
                .set(customer.nickname, nickname)
                .set(customer.introduce, introduce)
                .where(customer.customerId.eq(customerId))
                .execute();
    }
}