package com.ohgoodteam.ohgoodpay.shorts.repository.search;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;

public interface ShortsSearchRepository extends JpaRepository<ShortsEntity, Long>, ShortsSearchRepositoryCustom {
}
