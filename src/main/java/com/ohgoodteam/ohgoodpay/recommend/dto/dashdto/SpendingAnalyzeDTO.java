package com.ohgoodteam.ohgoodpay.recommend.dto.dashdto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SpendingAnalyzeDTO {

    // ---------- 요청 DTO ----------
    @Getter @ToString
    @Builder @Jacksonized
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class In {
        @JsonAlias({"customer_id","customerId"})
        private final Long customerId;
        // private final Integer windowMonths; // 필요 시 서비스에서 세팅
        private final String timezone;           // "Asia/Seoul"
        private final List<Transaction> transactions;

        @Getter @ToString
        @Builder @Jacksonized
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Transaction {
            @JsonAlias({"customer_id","customerId"})
            private final Long paymentId;
            private final String date;
            private final String requestName;
            private final BigDecimal amount;
            @JsonProperty("is_bnpl")
            private final Boolean isBnpl;       // 네이밍 이슈 방지
            private final String memo;
            private final String merchantCode;  // JSON: merchant_code
        }
    }

    // ---------- 응답 DTO ----------
    @Getter @ToString
    @Builder @Jacksonized
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Out {
        private final Summary summary;
        private final Map<String, MonthlyData> monthlyData; // JSON: monthly_data

        @Getter @ToString
        @Builder @Jacksonized
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Summary {
            private final Integer totalMonths;  // JSON: total_months
            private final DateRange dateRange;  // JSON: date_range
        }

        @Getter @ToString
        @Builder @Jacksonized
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class DateRange {
            private final String start;         // "YYYY-MM"
            private final String end;           // "YYYY-MM"
        }

        @Getter @ToString
        @Builder @Jacksonized
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class MonthlyData {
            private final BigDecimal totalSpend;                 // JSON: total_spend
            private final Map<String, CategoryStat> categories;
            private final List<TopTransaction> topTransactions;  // JSON: top_transactions
        }

        @Getter @ToString
        @Builder @Jacksonized
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class CategoryStat {
            private final BigDecimal amount;
            private final Double share;
            private final Integer rank;
        }

        @Getter @ToString
        @Builder @Jacksonized
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class TopTransaction {
            private final Long paymentId;
            private final String requestName;
            private final BigDecimal amount;
            private final String date;     // "YYYY-MM-DD"
            private final String category; // 한글 카테고리
        }
    }
}
