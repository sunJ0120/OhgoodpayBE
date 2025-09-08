package com.ohgoodteam.ohgoodpay.shorts.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;

@Repository
public interface ShortsUploadRepository extends JpaRepository<ShortsEntity, Long>{

}
