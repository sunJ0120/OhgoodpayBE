package com.ohgoodteam.ohgoodpay.pay.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static com.ohgoodteam.ohgoodpay.common.entity.QPaymentEntity.paymentEntity;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public long countValidInRange(Long customerId, LocalDateTime from, LocalDateTime to) {
        Long cnt = query.select(paymentEntity.count())
                .from(paymentEntity)
                .where(
                        paymentEntity.customer.customerId.eq(customerId),
                        paymentEntity.isExpired.isTrue(),
                        paymentEntity.date.goe(from),
                        paymentEntity.date.lt(to)
                )
                .fetchOne();
        return cnt == null ? 0L : cnt;
    }

    @Override
    public long sumAmountValidInRange(Long customerId, LocalDateTime from, LocalDateTime to) {
        // SUM(INT) -> BIGINT 반환을 안전하게 받기 위해 numberTemplate 사용
        Long sum = query.select(
                        Expressions.numberTemplate(Long.class, "COALESCE(SUM({0}),0)", paymentEntity.totalPrice)
                )
                .from(paymentEntity)
                .where(
                        paymentEntity.customer.customerId.eq(customerId),
                        paymentEntity.isExpired.isTrue(),
                        paymentEntity.date.goe(from),
                        paymentEntity.date.lt(to)
                )
                .fetchOne();
        return sum == null ? 0L : sum;
    }

    @Override
    public boolean existsExtensionThisMonth(Long customerId, LocalDateTime from, LocalDateTime to) {
        Integer exists = query.selectOne()
                .from(paymentEntity)
                .where(
                        paymentEntity.customer.customerId.eq(customerId),
                        paymentEntity.isExpired.isTrue(),
                        paymentEntity.date.goe(from),
                        paymentEntity.date.lt(to),
                        paymentEntity.requestName.in("EXTENSION", "RENEW", "연장")
                )
                .fetchFirst(); // 한 건만 확인
        return exists != null;
    }

    @Override
    public boolean existsAutoExtensionThisMonth(Long customerId, LocalDateTime from, LocalDateTime to) {
        Integer exists = query.selectOne()
                .from(paymentEntity)
                .where(
                        paymentEntity.customer.customerId.eq(customerId),
                        paymentEntity.isExpired.isTrue(),
                        paymentEntity.date.goe(from),
                        paymentEntity.date.lt(to),
                        paymentEntity.requestName.in("AUTO_EXTENSION", "자동연장")
                )
                .fetchFirst();
        return exists != null;
    }
}
