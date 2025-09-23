package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.OhgoodScorePayload;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("stub")
public class ScoreFeatureAssemblerStub implements ScoreFeatureAssembler {

    @Override
    public OhgoodScorePayload assemble(String customerId) {
        int h = Math.abs(customerId.hashCode());
        return new OhgoodScorePayload(
                (h % 5 == 0),           // extensionThisMonth
                (h % 7 == 0),           // autoExtensionThisMonth
                (h % 4),                // autoExtensionCnt12m
                55 + (h % 45),          // gradePoint 55~99
                false,                  // isBlocked
                10 + (h % 16),          // paymentCnt12m
                3_000_000L + (h % 5) * 1_000_000L, // paymentAmount12m
                80_000L + (h % 250_000),           // currentCycleSpend
                300_000L               // cycleLimit
        );
    }
}
