package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

//import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeRequest;
//import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.SayMyNameDTO;

import java.util.Map;

public interface DashAiClient {
    SayMyNameDTO.Out sayMyName(SayMyNameDTO.In in);
//    SpendingAnalyzeResponse analyzeSpending(SpendingAnalyzeRequest req);
}
