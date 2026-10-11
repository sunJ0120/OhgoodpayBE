package com.ohgoodteam.ohgoodpay.llm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgoodteam.ohgoodpay.llm.dto.PromptSpecDto;
import com.ohgoodteam.ohgoodpay.llm.entity.PromptVersionEntity;
import com.ohgoodteam.ohgoodpay.llm.repository.PromptVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 활성 프롬프트(키별 가장 큰 version) 조회. 캐시는 병목이 확인되면 추가한다 (계약 8장). */
@Service
@RequiredArgsConstructor
public class PromptService {
    private final PromptVersionRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public PromptSpecDto getActive(String promptKey) {
        PromptVersionEntity e = repository.findFirstByPromptKeyOrderByVersionDesc(promptKey)
                .orElseThrow(() -> new IllegalStateException("활성 프롬프트가 없습니다: " + promptKey));
        try {
            return new PromptSpecDto(e.getPromptKey(), e.getVersion(), e.getContent(),
                    objectMapper.readTree(e.getResponseSchema()), e.getModel());
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("프롬프트 응답 스키마가 JSON 이 아닙니다: " + promptKey + " v" + e.getVersion(), ex);
        }
    }
}
