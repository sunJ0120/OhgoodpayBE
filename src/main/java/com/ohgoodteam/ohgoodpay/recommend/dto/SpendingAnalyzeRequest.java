package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SpendingAnalyzeRequest {
    private List<TxnIn> transactions;

    @JsonProperty("use_llm_fallback")
    @Builder.Default
    private Boolean useLlmFallback = false;

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TxnIn {
        private String id;
        private String ts;
        private Double amount;
        private String currency;
        private String status;

        @JsonProperty("merchant_name") private String merchantName;
        private String mcc;
        private String channel;

        @JsonProperty("is_bnpl") private Boolean isBnpl;
        private Integer installments;
        private String memo;
        private String category; // FINE/MAIN 모두 문자열 허용
    }
}