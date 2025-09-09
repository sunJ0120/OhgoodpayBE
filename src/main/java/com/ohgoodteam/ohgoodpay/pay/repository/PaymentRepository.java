package com.ohgoodteam.ohgoodpay.pay.repository;

import com.ohgoodteam.ohgoodpay.common.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByPaymentRequest_OrderId(String orderId);

    // 결제내역 만료 처리
    @Transactional
    @Modifying
    @Query("UPDATE PaymentEntity p SET p.isExpired = true WHERE p.paymentId = :paymentId")
    int expirePayment(Long paymentId);

}
