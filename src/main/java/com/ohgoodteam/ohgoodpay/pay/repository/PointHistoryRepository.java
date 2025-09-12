package com.ohgoodteam.ohgoodpay.pay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgoodteam.ohgoodpay.common.entity.PointHistoryEntity;

public interface PointHistoryRepository extends JpaRepository<PointHistoryEntity, Long> {
    
    List<PointHistoryEntity> findByCustomerCustomerIdOrderByDateDesc(Long customerId);
}
