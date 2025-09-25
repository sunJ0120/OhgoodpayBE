package com.ohgoodteam.ohgoodpay.pay.repository;

import com.ohgoodteam.ohgoodpay.common.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long>, PaymentRepositoryCustom  {

    Optional<PaymentEntity> findByPaymentRequest_OrderId(String orderId);

    // 결제내역 만료 처리
    @Transactional
    @Modifying
    @Query("UPDATE PaymentEntity p SET p.isExpired = true WHERE p.paymentId = :paymentId")
    int expirePayment(Long paymentId);

    // 고객의 결제 내역 조회
    List<PaymentEntity> findByCustomerCustomerIdOrderByDateDesc(Long customerId);

    // 고객의 결제 내역 조회 (만료되지 않은 결제 내역)
    List<PaymentEntity> findByCustomerCustomerIdAndIsExpiredFalseOrderByDateAsc(Long customerId);

    // 결제 단일 건 조회
    PaymentEntity findByPaymentId(Long paymentId);

    // 결제건 납부 처리 업데이트
    @Transactional
    @Modifying
    @Query("UPDATE PaymentEntity p SET p.isExpired = :isExpired WHERE p.paymentId IN :paymentIds")
    int updatePaymentIsExpiredByPaymentId(boolean isExpired, Long[] paymentIds);

    // 해당 월 미납부 결제 조회
    @Query("SELECT p FROM PaymentEntity p WHERE p.customer.customerId = :customerId AND p.isExpired = false AND FUNCTION('YEAR', p.date) = :year AND FUNCTION('MONTH', p.date) = :month")
    List<PaymentEntity> findByCustomerCustomerIdAndIsExpiredFalseAndYearMonth(Long customerId, int year, int month);

    // 결제건 금액 산출, point 산출 등 사용
    @Query("SELECT SUM(p.price) FROM PaymentEntity p WHERE p.paymentId IN :paymentIds")
    int sumPriceByPaymentId(Long[] paymentIds);

}
