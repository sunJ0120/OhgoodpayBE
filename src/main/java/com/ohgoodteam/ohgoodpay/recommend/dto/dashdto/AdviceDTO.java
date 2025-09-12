// AdviceDTO.java
package com.ohgoodteam.ohgoodpay.recommend.dto.dashdto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AdviceDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class In {
        private Identity identity;
        private Spending spending;

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Identity {
            private Long customerId;
            private String username;
            private String tier;             // grade / gradeName 중 택1
            private Integer gradePoint;
            private Boolean autoExtensionThisMonth;
            private Integer autoExtensionCnt12m;
            private Boolean blocked;
            private Integer paymentCnt12m;
            private BigDecimal paymentAmount12m;
            private BigDecimal currentCycleSpend;
        }

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Spending {
            private Map<String,String> dateRange;     // {"start","end"}
            private String latestMonth;               // "YYYY-MM"
            private BigDecimal latestTotalSpend;
            private Double momGrowth;          
            private Boolean spikeFlag;
            private List<Category> topCategoriesLatest;
            private Map<String, Double> categoriesShareLatest;

            @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
            @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
            public static class Category {
                private String category;
                private BigDecimal amount;
                private Double share;
            }
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Out {
        private List<Advice> advices;
        private Map<String,String> meta;

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Advice {
            private String id;
            private String title;
            private String body;
            private String level;
            private List<String> tags;
            private List<String> refs;
        }
    }
}
