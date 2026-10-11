package com.ohgoodteam.ohgoodpay.llm.repository;

import com.ohgoodteam.ohgoodpay.llm.entity.PromptVersionEntity;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * 읽기 전용. JpaRepository/CrudRepository 를 상속하지 않는 이유: 프롬프트 버전은 수정·삭제하지 않기 때문이다.
 * 새 버전은 Flyway 마이그레이션(INSERT)으로만 추가한다.
 */
public interface PromptVersionRepository extends Repository<PromptVersionEntity, Long> {

    /** 활성 버전 = 키별 가장 큰 version */
    Optional<PromptVersionEntity> findFirstByPromptKeyOrderByVersionDesc(String promptKey);

    Optional<PromptVersionEntity> findByPromptKeyAndVersion(String promptKey, int version);
}
