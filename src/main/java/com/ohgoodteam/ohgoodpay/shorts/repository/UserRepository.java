package com.ohgoodteam.ohgoodpay.shorts.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;

public interface UserRepository extends JpaRepository<CustomerEntity, Long> {
    CustomerEntity findByCustomerId(Long customerId);
}
