package com.ohgoodteam.ohgoodpay.shorts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;

@Repository
public interface ShortsMypageRepository extends JpaRepository<ShortsEntity, Long>{

    // 내가 구독하는 사람 수
    long countByCustomerCustomerId(Long userId);
}
