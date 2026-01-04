package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.OhgoodScorePayload;

public interface ScoreFeatureAssembler {
    OhgoodScorePayload assemble(String customerId);
}