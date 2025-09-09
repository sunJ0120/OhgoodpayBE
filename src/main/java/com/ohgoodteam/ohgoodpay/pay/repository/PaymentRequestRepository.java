package com.ohgoodteam.ohgoodpay.pay.repository;

import com.ohgoodteam.ohgoodpay.common.entity.PaymentRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PaymentRequestRepository extends JpaRepository<PaymentRequestEntity, Long> {
    Optional<PaymentRequestEntity> findByQrcode(String qrcode);
    Optional<PaymentRequestEntity> findByPincode(String pincode);

    // 결제 요청 만료 처리
    @Transactional
    @Modifying
    @Query("UPDATE PaymentRequestEntity p SET p.isValidated = false WHERE p.paymentRequestId = :requestId")
    int expirePaymentRequest(Long requestId);

    boolean existsByQrcodeAndIsValidatedTrue(String qrcode);
    boolean existsByPincodeAndIsValidatedTrue(String pincode);
}
