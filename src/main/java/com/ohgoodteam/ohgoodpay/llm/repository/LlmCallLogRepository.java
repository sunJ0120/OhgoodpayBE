package com.ohgoodteam.ohgoodpay.llm.repository;

import com.ohgoodteam.ohgoodpay.llm.entity.LlmCallLogEntity;
import org.springframework.data.repository.Repository;

import java.util.List;

/** 추가(append)와 조회만 한다. */
public interface LlmCallLogRepository extends Repository<LlmCallLogEntity, Long> {

    <S extends LlmCallLogEntity> List<S> saveAll(Iterable<S> entities);

    List<LlmCallLogEntity> findByConversationIdOrderByIdAsc(String conversationId);
}
