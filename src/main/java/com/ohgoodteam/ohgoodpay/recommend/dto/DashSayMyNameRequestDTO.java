package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@ToString
@Builder
@Jacksonized
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DashSayMyNameRequestDTO {
    @JsonAlias({"customer_id","customerId"})
    private final Long customerId;

    public static DashSayMyNameRequestDTO of(Long customerId) {
        return builder().customerId(customerId).build();
    }
}
