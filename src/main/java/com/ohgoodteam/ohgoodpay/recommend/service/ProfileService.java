package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.ProfileSummaryDTO;

public interface ProfileService {
    ProfileSummaryDTO getProfile(Long customerId);

}
