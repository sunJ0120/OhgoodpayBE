-- 프롬프트 버전 관리와 LLM 호출 기록 (애플리케이션 DB 에 함께 둔다).
-- 별도 스키마를 쓰지 않는 이유: MySQL 에서 schema 는 database 와 같고, Hibernate MySQL 방언은 @Table(schema)를 무시한다.

-- 프롬프트 버전. 한 번 저장한 행은 수정·삭제하지 않는다 (새 버전은 새 마이그레이션의 INSERT 로만 추가).
-- 활성 버전 = prompt_key 별 MAX(version)
CREATE TABLE prompt_version (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    prompt_key      VARCHAR(100) NOT NULL,
    version         INT          NOT NULL,
    content         MEDIUMTEXT   NOT NULL,
    response_schema JSON         NOT NULL,
    model           VARCHAR(100) NOT NULL,
    change_note     VARCHAR(255) NOT NULL,
    created_at      DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_prompt_version_key_version UNIQUE (prompt_key, version)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- LLM 호출 1건당 1행. 사용자 원문은 저장하지 않는다 (메타데이터만).
CREATE TABLE llm_call_log (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    conversation_id VARCHAR(100) NOT NULL,
    flow_step       VARCHAR(50)  NOT NULL,
    slot            VARCHAR(20)  NOT NULL,
    request_id      VARCHAR(64)  NOT NULL,
    prompt_key      VARCHAR(100) NOT NULL,
    prompt_version  INT          NOT NULL,
    model           VARCHAR(100) NOT NULL,
    latency_ms      INT          NOT NULL,
    tokens_in       INT          NOT NULL,
    tokens_out      INT          NOT NULL,
    valid           TINYINT(1)   NOT NULL,
    error_code      VARCHAR(50)  NULL,
    created_at      DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_llm_call_log_created_at (created_at),
    INDEX idx_llm_call_log_prompt (prompt_key, prompt_version),
    INDEX idx_llm_call_log_conversation (conversation_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
