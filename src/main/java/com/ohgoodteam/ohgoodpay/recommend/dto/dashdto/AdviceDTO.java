package com.ohgoodteam.ohgoodpay.recommend.dto.dashdto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AdviceDTO {

    // ---------- 요청 DTO ----------
    @Getter @ToString
    @Builder @Jacksonized
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class In {
        private final Identity identity;
        private final Spending spending;

        @Getter @ToString
        @Builder @Jacksonized
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Identity {
            @JsonAlias({"customer_id","customerId"})
            private final Long customerId;
            private final String username;
            private final String tier;
            private final Integer gradePoint;
            private final Boolean autoExtensionThisMonth;
            private final Integer autoExtensionCnt12m;
            private final Boolean blocked;
            private final Integer paymentCnt12m;
            private final BigDecimal paymentAmount12m;
            private final BigDecimal currentCycleSpend;
        }

        @Getter @ToString
        @Builder @Jacksonized
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Spending {
            private final Map<String,String> dateRange;
            private final String latestMonth;
            private final BigDecimal latestTotalSpend;
            private final Double momGrowth;
            private final Boolean spikeFlag;
            private final List<Category> topCategoriesLatest;
            private final Map<String, Double> categoriesShareLatest;

            @Getter @ToString
            @Builder @Jacksonized
            @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
            public static class Category {
                private final String category;
                private final BigDecimal amount;
                private final Double share;
            }
        }
    }

    // ---------- 응답 DTO ----------
    @Getter @ToString
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Out {
        private final List<Advice> advices;
        private final Map<String,String> meta;

        @Getter @ToString
        @Builder
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Advice {
            private final String id;
            private final String title;
            private final String body;
            private final String level;
            private final List<String> tags;
            private final List<String> refs;
        }
    }
}
