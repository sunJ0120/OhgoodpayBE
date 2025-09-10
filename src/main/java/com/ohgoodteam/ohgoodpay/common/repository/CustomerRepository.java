package com.ohgoodteam.ohgoodpay.common.repository;

import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    
}
