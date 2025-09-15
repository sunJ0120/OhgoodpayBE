package com.ohgoodteam.ohgoodpay.recommend.dto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ohgoodteam.ohgoodpay.pay.dto.PaymentViewDTO;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PayThisMonthResponseDTO(
        Long customerId,
        String month,
        java.time.LocalDateTime from,
        java.time.LocalDateTime to,
        int count,
        long sumTotalPrice,
        java.util.List<PaymentViewDTO> items   // 이미 record라 OK
) { }