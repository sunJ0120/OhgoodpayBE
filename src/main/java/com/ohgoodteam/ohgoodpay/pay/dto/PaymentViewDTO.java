package com.ohgoodteam.ohgoodpay.pay.dto;

import java.time.LocalDateTime;

/** 결제 내역 조회 전용 */
public record PaymentViewDTO(
        Long paymentId,
        LocalDateTime date,
        String requestName,
        Integer totalPrice
) {}
