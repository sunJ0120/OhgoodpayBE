package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeRequest;
import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeResponse;

public interface SpendingAnalyzeService {
    SpendingAnalyzeResponse execute(Long customerId);
    SpendingAnalyzeResponse execute(SpendingAnalyzeRequest req);

}