package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

import com.ohgoodteam.ohgoodpay.recommend.dto.DashSayMyNameResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeRequest;
import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.SayMyNameDTO;

import java.util.Map;

public interface DashAiClient {
    DashSayMyNameResponse sayMyName(Map<String, Object> payload);
    SpendingAnalyzeResponse analyzeSpending(SpendingAnalyzeRequest req);
}
