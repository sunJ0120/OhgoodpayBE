package com.ohgoodteam.ohgoodpay.llm.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

/**
 * LLM 호출 1건의 메타데이터. 사용자 원문은 저장하지 않는다 (개인정보가 섞일 수 있음).
 */
@Entity
@Immutable
@Table(name = "llm_call_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LlmCallLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversation_id", nullable = false)
    private String conversationId;

    @Column(name = "flow_step", nullable = false)
    private String flowStep;

    @Column(nullable = false)
    private String slot;

    @Column(name = "request_id", nullable = false)
    private String requestId;

    @Column(name = "prompt_key", nullable = false)
    private String promptKey;

    @Column(name = "prompt_version", nullable = false)
    private int promptVersion;

    @Column(nullable = false)
    private String model;

    @Column(name = "latency_ms", nullable = false)
    private int latencyMs;

    @Column(name = "tokens_in", nullable = false)
    private int tokensIn;

    @Column(name = "tokens_out", nullable = false)
    private int tokensOut;

    @Column(nullable = false)
    private boolean valid;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private LlmCallLogEntity(String conversationId, String flowStep, String slot, String requestId,
                             String promptKey, int promptVersion, String model, int latencyMs,
                             int tokensIn, int tokensOut, boolean valid, String errorCode) {
        this.conversationId = conversationId;
        this.flowStep = flowStep;
        this.slot = slot;
        this.requestId = requestId;
        this.promptKey = promptKey;
        this.promptVersion = promptVersion;
        this.model = model;
        this.latencyMs = latencyMs;
        this.tokensIn = tokensIn;
        this.tokensOut = tokensOut;
        this.valid = valid;
        this.errorCode = errorCode;
        this.createdAt = LocalDateTime.now();
    }
}
