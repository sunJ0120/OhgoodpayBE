package com.ohgoodteam.ohgoodpay.shorts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.shorts.repository.profile.ShortsProfileRepositoryCustom;

@Repository
public interface ShortsProfileRepository extends JpaRepository<CustomerEntity, Long>, ShortsProfileRepositoryCustom {
}
