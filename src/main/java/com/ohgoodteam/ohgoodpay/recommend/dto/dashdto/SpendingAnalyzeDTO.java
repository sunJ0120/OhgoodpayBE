package com.ohgoodteam.ohgoodpay.recommend.dto.dashdto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SpendingAnalyzeDTO {

    // ---------- FastAPI 요청 ----------
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class In {
        private Long customerId;
//        private Integer windowMonths;      // 기본 3 (서비스에서 세팅)
        private String timezone;           // "Asia/Seoul"
        private List<Transaction> transactions;

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Transaction {
            private Long paymentId;       // 전역 SNAKE_CASE면 JSON에서는 payment_id
            private String date;          // "YYYY-MM-DD"
            private String requestName;   // request_name
            private BigDecimal amount;
            private Boolean isBnpl;       // is_bnpl
            private String memo;
            private String merchantCode;  // merchant_code
        }
    }

    // ---------- FastAPI 응답 ----------
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Out {
        private Summary summary;
        private Map<String, MonthlyData> monthlyData; // JSON: monthly_data

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Summary {
            private Integer totalMonths;  // total_months
            private DateRange dateRange;  // date_range
        }

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class DateRange {
            private String start;         // "YYYY-MM"
            private String end;           // "YYYY-MM"
        }

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class MonthlyData {
            private BigDecimal totalSpend;                         // total_spend
            private Map<String, CategoryStat> categories;          // "식비": { ... }
            private List<TopTransaction> topTransactions;          // top_transactions
        }

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class CategoryStat {
            private BigDecimal amount;
            private Double share;
            private Integer rank;
        }

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class TopTransaction {
            private Long paymentId;
            private String requestName;
            private BigDecimal amount;
            private String date;     // "YYYY-MM-DD"
            private String category; // 한글 카테고리
        }
    }
}
