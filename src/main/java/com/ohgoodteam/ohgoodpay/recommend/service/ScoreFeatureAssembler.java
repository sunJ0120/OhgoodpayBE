package com.ohgoodteam.ohgoodpay.recommend.service;

public interface ScoreFeatureAssembler {
    OhgoodScorePayload assemble(String customerId);
}