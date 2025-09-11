// com.ohgoodteam.ohgoodpay.pay.repository.PaymentRepositoryCustom
package com.ohgoodteam.ohgoodpay.pay.repository;

import java.time.LocalDateTime;

public interface PaymentRepositoryCustom {
    // 유효 결제 건수
    long   countValidInRange(Long customerId, LocalDateTime from, LocalDateTime to);
    // 유효 결제 금액 합계
    long   sumAmountValidInRange(Long customerId, LocalDateTime from, LocalDateTime to);
    // 이번달 연장 여부
    boolean existsExtensionThisMonth(Long customerId, LocalDateTime from, LocalDateTime to);
    // 이번달 자동연장 여부
    boolean existsAutoExtensionThisMonth(Long customerId, LocalDateTime from, LocalDateTime to);
}
