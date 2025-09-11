package com.ohgoodteam.ohgoodpay.recommend.dto.dashdto;

import lombok.*;

public class SayMyNameDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
    public static class In {
        private Long customerId;
        private String username;
        private String grade;
        private boolean extensionThisMonth;
        private boolean autoExtensionThisMonth;
        private int     autoExtensionCnt12m;
        private int     gradePoint;
        private boolean isBlocked;
        private int     paymentCnt12m;
        private double  paymentAmount12m;
        private double  currentCycleSpend;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
    public static class Out {
        private String  message;     // FastAPI가 내려줌
        private String  sessionId;   // camelCase 그대로 내려오므로 그대로 매핑
        private Integer ttlSeconds;
        private Integer score;       // FastAPI가 내려줌 (서비스에서 ohgoodScore로 매핑)
        private String  userId;
        private String  gradeName;
        private Integer gradeLimit;
        private Double  pointPercent;
    }
}
