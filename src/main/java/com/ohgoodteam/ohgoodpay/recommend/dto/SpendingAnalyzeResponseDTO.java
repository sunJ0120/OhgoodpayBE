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
        private Map<String, CategoryStat> categories; // "식비" 등 한글 카테고리 → 통계
        private List<TopTransaction> top_transactions;
    }

    @Getter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class CategoryStat {
        private BigDecimal amount; // 합계
        private Double share;      // 0~1 비율(소수 4자리)
        private Integer rank;      // 1,2,3...
    }

    @Getter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class TopTransaction {
        private Long payment_id;
        private String request_name; // 가맹점/요청사
        private BigDecimal amount;
        private String date;         // "YYYY-MM-DD"
        private String category;     // 한글 카테고리(선택)
    }
}
