package com.ohgoodteam.ohgoodpay.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    
}
