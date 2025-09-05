package com.ohgoodteam.ohgoodpay.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;

import jakarta.transaction.Transactional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    
    // 회원 수동 연장 업데이트
    @Transactional
    @Modifying
    @Query("UPDATE CustomerEntity c SET c.isExtension = :isExtension WHERE c.customerId = :customerId")
    int updateCustomerIsExtension(boolean isExtension, Long customerId);

    // 회원 자동 연장 업데이트
    @Transactional
    @Modifying
    @Query("UPDATE CustomerEntity c SET c.isExtension = true, c.isAuto = :isAuto WHERE c.customerId = :customerId")
    int updateCustomerIsAuto(boolean isAuto, Long customerId);
}
