package com.ohgoodteam.ohgoodpay.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;

import jakarta.transaction.Transactional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    
    // customerId로 회원 조회
    CustomerEntity findByCustomerId(Long customerId);

    // customerId로 회원 연장 상태 업데이트
    @Transactional
    @Modifying
    @Query("UPDATE CustomerEntity c SET c.isExtension = :isExtension WHERE c.customerId = :customerId")
    int updateCustomerIsExtension(boolean isExtension, Long customerId);

    // customerId로 회원 자동 연장 상태 업데이트
    @Transactional
    @Modifying
    @Query("UPDATE CustomerEntity c SET c.isAuto = :isAuto, c.extensionCnt = c.extensionCnt + 1 WHERE c.customerId = :customerId")
    int updateCustomerIsAuto(boolean isAuto, Long customerId);

    // customerId로 회원 gradePoint 업데이트
    @Transactional
    @Modifying
    @Query("UPDATE CustomerEntity c SET c.gradePoint = c.gradePoint + :gradePoint WHERE c.customerId = :customerId")
    int updateCustomerGradePoint(int gradePoint, Long customerId);

    // gradePoint 로 grade_name 업데이트
    @Transactional
    @Modifying
    @Query("UPDATE CustomerEntity c SET c.grade.gradeName = :gradeName WHERE c.customerId = :customerId")
    int updateCustomerGradeName(String gradeName, Long customerId);
}
