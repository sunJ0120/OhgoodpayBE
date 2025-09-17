package com.ohgoodteam.ohgoodpay.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    

    // customerId로 회원 조회
    CustomerEntity findByCustomerId(Long customerId);

    // emailId로 회원 조회
    CustomerEntity findByEmailId(String emailId);

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

    // customerId로 회원 balance 업데이트
    @Transactional
    @Modifying
    @Query("UPDATE CustomerEntity c SET c.balance = c.balance + :balance WHERE c.customerId = :customerId")
    int plusCustomerBalance(int balance, Long customerId);


    // customerId로 회원 point 차감
    @Transactional
    @Modifying
    @Query("UPDATE CustomerEntity c SET c.point = c.point - :point WHERE c.customerId = :customerId")
    int minusCustomerPoint(int point, Long customerId);

    //customerId로 회원 point 적립
    @Transactional
    @Modifying
    @Query("UPDATE CustomerEntity c SET c.point = c.point + :point WHERE c.customerId = :customerId")
    int plusCustomerPoint(int point, Long customerId);

    //customerId로 회원 balance 차감
    @Transactional
    @Modifying
    @Query("UPDATE CustomerEntity c SET c.balance = c.balance - :balance WHERE c.customerId = :customerId")
    int minusCustomerBalance(int balance, Long customerId);

}
