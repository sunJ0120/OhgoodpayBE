// SpendingReportResponse.java
package com.ohgoodteam.ohgoodpay.recommend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class SpendingAnalyzeResponseDTO {
    private Summary summary;
    private Map<String, MonthlyData> monthly_data; // "YYYY-MM" -> 월별 블록

    @Getter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class Summary {
        private Integer total_months;
        private DateRange date_range;
    }
    @Getter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class DateRange {
        private String start; // "YYYY-MM"
        private String end;   // "YYYY-MM"
    }

    @Getter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class MonthlyData {
        private BigDecimal total_spend;
        private Map<String, CategoryStat> categories; 
        private List<TopTransaction> top_transactions;
    }

    @Getter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class CategoryStat {
        private BigDecimal amount;
        private Double share;
        private Integer rank;
    }

    @Getter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class TopTransaction {
        private Long payment_id;
        private String request_name;
        private BigDecimal amount;
        private String date;
        private String category;
    }
}
