package com.ohgoodteam.ohgoodpay.pay.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgoodteam.ohgoodpay.common.entity.GradeEntity;

public interface GradeRepository extends JpaRepository<GradeEntity, Long> {
    
}
