package com.ohgoodteam.ohgoodpay.pay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ohgoodteam.ohgoodpay.common.entity.GradeEntity;
import com.ohgoodteam.ohgoodpay.pay.dto.GradeDTO;

@Repository
public interface GradeRepository extends JpaRepository<GradeEntity, Long> {
    // 등급 조회
    GradeEntity findByGradeName(String gradeName);
}
