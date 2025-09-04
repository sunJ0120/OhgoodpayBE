package com.ohgoodteam.ohgoodpay.pay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgoodteam.ohgoodpay.common.entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    // 고객의 결제 내역 조회
    List<PaymentEntity> findByCustomerCustomerId(Long customerId);

    // 고객의 결제 내역 조회 (만료되지 않은 결제 내역)
    List<PaymentEntity> findByCustomerCustomerIdAndIsExpiredFalse(Long customerId);

    // 결제 단일 건 조회
    PaymentEntity findByPaymentId(Long paymentId);
}
