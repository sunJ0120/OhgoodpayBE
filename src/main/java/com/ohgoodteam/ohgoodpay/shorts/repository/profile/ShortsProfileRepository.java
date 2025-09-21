package com.ohgoodteam.ohgoodpay.shorts.repository.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;

@Repository
public interface ShortsProfileRepository extends JpaRepository<CustomerEntity, Long>, ShortsProfileRepositoryCustom {
}
