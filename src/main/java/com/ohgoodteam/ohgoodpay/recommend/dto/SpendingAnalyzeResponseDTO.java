package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.Map;

@Getter @ToString
@Builder @Jacksonized
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SpendingAnalyzeResponseDTO {

    private final Summary summary;
    private final Map<String, MonthlyData> monthly_data;

    @Getter @ToString
    @Builder @Jacksonized
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Summary {
        private final DateRange date_range;
    }

    @Getter @ToString
    @Builder @Jacksonized
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class DateRange {
        private final String start;
        private final String end;
    }

    @Getter @ToString
    @Builder @Jacksonized
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class MonthlyData {
        private final BigDecimal total_spend;
        private final Map<String, CategoryData> categories;
    }

    @Getter @ToString
    @Builder @Jacksonized
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CategoryData {
        private final BigDecimal amount;
        private final Double share;
        private final String rank;
    }
}
