package com.ohgoodteam.ohgoodpay.recommend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AdviceRequestDTO {
    @NotNull @Positive
    private Long customerId;
}