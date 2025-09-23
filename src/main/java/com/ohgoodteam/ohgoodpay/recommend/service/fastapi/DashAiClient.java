package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.AdviceDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.SayMyNameDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.SpendingAnalyzeDTO;

import java.util.Map;

public interface DashAiClient {
    SayMyNameDTO.Out sayMyName(SayMyNameDTO.In in);
    SpendingAnalyzeDTO.Out analyzeSpending(SpendingAnalyzeDTO.In in); //
    AdviceDTO.Out getAdvice(AdviceDTO.In snapshot);
}
