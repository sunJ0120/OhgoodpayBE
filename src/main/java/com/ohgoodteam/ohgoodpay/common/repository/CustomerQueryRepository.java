package com.ohgoodteam.ohgoodpay.common.repository;

import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDTO;

public interface CustomerQueryRepository {
    CustomerCacheDTO findCustomerCacheInfoById(Long customerId);
}
