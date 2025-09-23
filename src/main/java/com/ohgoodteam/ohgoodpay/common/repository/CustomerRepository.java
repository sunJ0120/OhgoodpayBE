package com.ohgoodteam.ohgoodpay.common.repository;
import java.util.List;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long>, CustomerQueryRepository{

    Optional<CustomerEntity> findByCustomerId(Long customerId);
    
    /**
     * 고객 취미 업데이트 (DDD Command 패턴)
     */
    @Modifying
    @Query("UPDATE CustomerEntity c SET c.hobby = :hobby WHERE c.customerId = :customerId")
    int updateHobbyByCustomerId(@Param("customerId") Long customerId, @Param("hobby") String hobby);

    /*
    * 점수 입력에 필요한 고객 플래그/점수 뷰
     */
    interface FlagsView {
        Boolean getIsBlocked();
        Boolean getIsAuto();
        Boolean getIsExtension();
        Integer getGradePoint();
        String  getExtensionCnt(); // VARCHAR → 서비스에서 int 변환
    }

    @Query("""
        SELECT c.isBlocked   AS isBlocked,
               c.isAuto      AS isAuto,
               c.isExtension AS isExtension,
               c.gradePoint  AS gradePoint,
               c.extensionCnt AS extensionCnt
          FROM CustomerEntity c
         WHERE c.customerId = :customerId
    """)
    FlagsView findFlagsById(@Param("customerId") Long customerId);

    @Query("select c.point from CustomerEntity c where c.customerId = :customerId")
    Optional<Integer> findPointByCustomerId(Long customerId);

    @Query("SELECT c.hobby FROM CustomerEntity c WHERE c.customerId = :customerId")
    Optional<String> findHobbyByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT c.balance FROM CustomerEntity c WHERE c.customerId = :customerId")
    Optional<Integer> findBalanceByCustomerId(@Param("customerId") Long customerId);

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

    //customerId로 회원 등급으로 balance 초기화
    @Transactional
    @Modifying
    @Query("UPDATE CustomerEntity c SET c.balance = :balance WHERE c.customerId = :customerId")
    int updateCustomerBalance(int balance, Long customerId);

    //연장 신청 상태 유저 조회
    List<CustomerEntity> findByIsExtensionTrue();

    //연장 미신청 상태 유저 조회
    List<CustomerEntity> findByIsExtensionFalse(); 

    //customerId로 회원 제재 상태 업데이트
    @Transactional
    @Modifying
    @Query("UPDATE CustomerEntity c SET c.isBlocked = :isBlocked WHERE c.customerId = :customerId")
    int updateCustomerIsBlocked(boolean isBlocked, Long customerId);

}

