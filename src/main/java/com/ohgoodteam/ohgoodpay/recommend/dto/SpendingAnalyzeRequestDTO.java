package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter @ToString
@Builder @Jacksonized
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SpendingAnalyzeRequestDTO {
    @JsonAlias({"customer_id","customerId"})
    private final Long customerId;
    @Builder.Default private final Integer windowMonths = 3;

    public static SpendingAnalyzeRequestDTO ofDefault3m(Long customerId) {
        return builder().customerId(customerId).windowMonths(3).build();
    }
    public static SpendingAnalyzeRequestDTO of(Long customerId, int windowMonths) {
        return builder().customerId(customerId).windowMonths(windowMonths).build();
    }
}
