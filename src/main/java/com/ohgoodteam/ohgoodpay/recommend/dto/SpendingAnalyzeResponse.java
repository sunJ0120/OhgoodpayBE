package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpendingAnalyzeResponse {
    private List<MonthSummary> months;
    private Double mom_growth; // null 가능
    private List<Object> spikes; // 필요 시 세부 DTO로 교체 가능
    private List<TopTransaction> top_transactions_3m;
    private List<CategoryAmountMain> top_categories_3m;

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MonthSummary {
        private String month;
        private Double total_spend;
        private Map<String, Double> by_category;     // "식비": 12345.0
        private Map<String, Double> category_share;  // "식비": 0.34
        private List<TopTransaction> top_transactions;
        private List<CategoryAmountMain> top_categories;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TopTransaction {
        private String id;
        private String ts;
        private String merchant_name;
        private Double amount;
        private String category; // MainCategory 문자열 ("식비" 등)
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CategoryAmountMain {
        private String category; // MainCategory 문자열
        private Double amount;
        private Double share;
    }
}
