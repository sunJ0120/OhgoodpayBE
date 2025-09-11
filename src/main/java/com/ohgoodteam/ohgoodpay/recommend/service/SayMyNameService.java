package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.DashSayMyNameResponseDTO;

public interface SayMyNameService {
    DashSayMyNameResponseDTO execute(Long customerId);
}
