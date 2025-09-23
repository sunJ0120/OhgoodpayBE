package com.ohgoodteam.ohgoodpay.common.repository;

import com.ohgoodteam.ohgoodpay.common.dto.CustomerCacheDTO;

public interface CustomerQueryRepository {
    CustomerCacheDTO findCustomerCacheInfoById(Long customerId);
}
