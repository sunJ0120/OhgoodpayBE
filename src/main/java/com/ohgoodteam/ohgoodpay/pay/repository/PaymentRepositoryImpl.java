package com.ohgoodteam.ohgoodpay.pay.repository;

import com.ohgoodteam.ohgoodpay.pay.dto.PaymentViewDTO;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
                .fetchFirst();
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

    @PersistenceContext
    private EntityManager em; // final 금지

    @Override
    public List<PaymentViewDTO> findRecentByCustomer(Long customerId, LocalDateTime start, LocalDateTime end) {
        String jpql = """
        select new com.ohgoodteam.ohgoodpay.pay.dto.PaymentViewDTO(
            p.paymentId,
            p.date,
            p.requestName,
            p.totalPrice,
            p.point
        )
        from PaymentEntity p
        where p.customer.customerId = :cid
          and p.isExpired = true
          and p.date >= :start and p.date < :end
        order by p.date asc
    """;
        return em.createQuery(jpql, PaymentViewDTO.class)
                .setParameter("cid", customerId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }


    @Override
    public long sumPointValidInRange(Long customerId, LocalDateTime from, LocalDateTime to) {
        Long sum = query.select(
                        Expressions.numberTemplate(Long.class, "COALESCE(SUM({0}),0)", paymentEntity.point)
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
}
