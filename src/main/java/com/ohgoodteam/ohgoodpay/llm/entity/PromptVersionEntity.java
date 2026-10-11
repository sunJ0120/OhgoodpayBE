package com.ohgoodteam.ohgoodpay.llm.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

/**
 * 프롬프트 버전 1건. 읽기 전용이다 — 수정·삭제 수단을 코드에 두지 않는다.
 * 새 버전은 Flyway 마이그레이션의 INSERT 로만 추가한다 (db/migration).
 */
@Entity
@Immutable
@Table(name = "prompt_version")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromptVersionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prompt_key", nullable = false, updatable = false)
    private String promptKey;

    @Column(nullable = false, updatable = false)
    private int version;

    @Column(nullable = false, updatable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "response_schema", nullable = false, updatable = false, columnDefinition = "json")
    private String responseSchema;

    @Column(nullable = false, updatable = false)
    private String model;

    @Column(name = "change_note", nullable = false, updatable = false)
    private String changeNote;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
