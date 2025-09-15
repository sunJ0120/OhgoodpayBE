package com.ohgoodteam.ohgoodpay.pay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgoodteam.ohgoodpay.common.entity.PointHistoryEntity;

public interface PointHistoryRepository extends JpaRepository<PointHistoryEntity, Long> {
    
    // 고객 포인트 내역 조회
    List<PointHistoryEntity> findByCustomerCustomerIdOrderByDateDesc(Long customerId);
    
}
