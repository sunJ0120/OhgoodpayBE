package com.ohgoodteam.ohgoodpay.shorts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;
import com.ohgoodteam.ohgoodpay.shorts.repository.search.ShortsSearchRepositoryCustom;

public interface ShortsSearchRepository extends JpaRepository<ShortsEntity, Long>, ShortsSearchRepositoryCustom {
}
