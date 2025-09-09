package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.DashSayMyNameResponse;

public interface SayMyNameService {
    DashSayMyNameResponse execute(Long customerId);
}
