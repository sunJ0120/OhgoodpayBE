package com.ohgoodteam.ohgoodpay.pay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ohgoodteam.ohgoodpay.common.entity.PaymentEntity;

import jakarta.transaction.Transactional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    // 고객의 결제 내역 조회
    List<PaymentEntity> findByCustomerCustomerId(Long customerId);

    // 고객의 결제 내역 조회 (만료되지 않은 결제 내역)
    List<PaymentEntity> findByCustomerCustomerIdAndIsExpiredFalse(Long customerId);

    // 결제 단일 건 조회
    PaymentEntity findByPaymentId(Long paymentId);

    // 결제건 납부 처리 업데이트
    // @Transactional
    // @Modifying
    // @Query("UPDATE PaymentEntity p SET p.isExpired = :isExpired WHERE p.paymentId IN :paymentIds")
    // int updatePaymentIsExpiredByPaymentId(boolean isExpired, Long[] paymentIds);
}
