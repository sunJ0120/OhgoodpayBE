package com.ohgoodteam.ohgoodpay.pay.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgoodteam.ohgoodpay.common.entity.PointHistoryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointHistoryRepository extends JpaRepository<PointHistoryEntity, Long> {
    
    // 고객 포인트 내역 조회
    List<PointHistoryEntity> findByCustomerCustomerIdOrderByDateDesc(Long customerId);

    // 출석체크 중복 확인
    @Query(value = "SELECT COUNT(*) FROM point_history p " +
            "WHERE p.customer_id = :customerId " +
            "AND DATE(p.date) = CURDATE() " +
            "AND p.point_explain = '출석 체크'",
            nativeQuery = true)
    int countByCustomerAndToday(@Param("customerId") Long customerId);





}
