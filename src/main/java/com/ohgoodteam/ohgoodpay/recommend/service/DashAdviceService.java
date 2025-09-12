package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.DashSayMyNameResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeRequestDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.AdviceDTO;
import com.ohgoodteam.ohgoodpay.recommend.service.fastapi.DashAiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashAdviceService {

    private final DashAiClient dashAiClient;
    private final SayMyNameService sayMyNameService;
    private final SpendingAnalyzeService spendingAnalyzeService;

    /** customerId만 받아 전체 파이프라인 수행 */
    public AdviceDTO.Out generate(Long customerId) {
        // 고객 컨텍스트
        DashSayMyNameResponseDTO say = sayMyNameService.execute(customerId);

        // 지출 분석 요청 DTO 생성 (기본 windowMonths=3)
        SpendingAnalyzeRequestDTO spendReq = SpendingAnalyzeRequestDTO.builder()
                .customerId(customerId)
                .build();

        // 지출 분석 실행
        SpendingAnalyzeResponseDTO spend = spendingAnalyzeService.analyze(spendReq);

        // 스냅샷 조립 → FastAPI LLM 조언 요청
        AdviceDTO.In snapshot = AdviceSnapshotAssembler.fromResponses(customerId, say, spend);
        return dashAiClient.getAdvice(snapshot);
    }

    /** 이미 응답을 갖고 있으면 재사용 */
    public AdviceDTO.Out generateFrom(Long customerId,
                                      DashSayMyNameResponseDTO say,
                                      SpendingAnalyzeResponseDTO spend) {
        AdviceDTO.In snapshot = AdviceSnapshotAssembler.fromResponses(customerId, say, spend);
        return dashAiClient.getAdvice(snapshot);
    }
}
