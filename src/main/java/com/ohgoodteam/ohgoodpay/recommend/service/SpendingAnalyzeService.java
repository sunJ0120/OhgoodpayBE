package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeRequestDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeResponseDTO;

public interface SpendingAnalyzeService {
    SpendingAnalyzeResponseDTO analyze(SpendingAnalyzeRequestDTO req);

}