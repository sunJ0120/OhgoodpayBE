package com.ohgoodteam.ohgoodpay.shorts.repository.mypage;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;

@Repository
public interface ShortsMypageRepository extends JpaRepository<ShortsEntity, Long>{

    long countByCustomerCustomerId(Long userId);

   
    
}
