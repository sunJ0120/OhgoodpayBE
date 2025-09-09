package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.DashSayMyNameResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.ProfileSummaryDTO;
import com.ohgoodteam.ohgoodpay.recommend.service.fastapi.DashAiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SayMyNameServiceImpl implements SayMyNameService {

    private final ProfileService profileService;
    private final DashAiClient dashAiClient;

    // ★ Assembler가 없어도 컨테이너가 항상 제공해주는 Provider(빈 없어도 주입 가능)
    private final ObjectProvider<ScoreFeatureAssembler> assemblerProvider;

    @Override
    public DashSayMyNameResponse execute(Long customerId) {
        ProfileSummaryDTO p = profileService.getProfile(customerId);

        Map<String, Object> payload = new HashMap<>();

        // 1) 특징치: 있으면 사용, 없으면 기본값으로 조립
        ScoreFeatureAssembler assembler = assemblerProvider.getIfAvailable();
        if (assembler != null) {
            var f = assembler.assemble(String.valueOf(customerId));
            payload.put("extension_this_month",      f.extensionThisMonth());
            payload.put("auto_extension_this_month", f.autoExtensionThisMonth());
            payload.put("auto_extension_cnt_12m",    f.autoExtensionCnt12m());
            payload.put("grade_point",               f.gradePoint());
            payload.put("is_blocked",                f.isBlocked());
            payload.put("payment_cnt_12m",           f.paymentCnt12m());
            payload.put("payment_amount_12m",        f.paymentAmount12m());
            payload.put("current_cycle_spend",       f.currentCycleSpend());
            if (f.cycleLimit() != null) payload.put("cycle_limit", f.cycleLimit());
        } else {
            // ▼ 임시 기본값(지금 당장 테스트용) — 조인/계산 구현되면 제거
            payload.put("extension_this_month",      false);
            payload.put("auto_extension_this_month", false);
            payload.put("auto_extension_cnt_12m",    0);
            payload.put("grade_point",               p.getScore() != null ? p.getScore() : 62);
            payload.put("is_blocked",                false);
            payload.put("payment_cnt_12m",           12);
            payload.put("payment_amount_12m",        6_800_000);
            payload.put("current_cycle_spend",       180_000);
            payload.put("cycle_limit",               300_000);
        }

        // 2) 컨텍스트(메시지 참고용)
        payload.put("customer_id", p.getCustomerId());
        payload.put("username",    p.getUsername());
        payload.put("grade",       p.getGrade());
        payload.put("ohgood_score",p.getScore());

        // 3) FastAPI 호출
        return dashAiClient.sayMyName(payload);
    }
}
