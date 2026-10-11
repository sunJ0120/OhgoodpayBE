package com.ohgoodteam.ohgoodpay.llm;

import com.ohgoodteam.ohgoodpay.config.QuerydslConfig;
import com.ohgoodteam.ohgoodpay.llm.dto.MlCallMeta;
import com.ohgoodteam.ohgoodpay.llm.dto.PromptSpecDto;
import com.ohgoodteam.ohgoodpay.llm.entity.LlmCallLogEntity;
import com.ohgoodteam.ohgoodpay.llm.repository.LlmCallLogRepository;
import com.ohgoodteam.ohgoodpay.llm.service.LlmCallLogService;
import com.ohgoodteam.ohgoodpay.llm.service.PromptService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 실제 MySQL(Testcontainers)에 Flyway 마이그레이션을 적용해 llm 스키마를 검증한다.
 * Docker 가 없으면 건너뛴다 (disabledWithoutDocker).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@Import({PromptService.class, LlmCallLogService.class, QuerydslConfig.class})
@Testcontainers(disabledWithoutDocker = true)
class LlmSchemaMigrationTest {

    static final Set<String> EXPECTED_KEYS = Set.of(
            "validate_mood", "validate_hobby", "validate_choose", "validate_recommendation",
            "validate_re_recommendation", "validate_default",
            "chat_hobby_check_with_hobby", "chat_hobby_check_empty", "chat_choose", "chat_fallback",
            "keyword_extract", "summary_update");

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("ohgoodpay").withUsername("test").withPassword("test")
            // 운영과 같은 상황: 이미 JPA 테이블이 있는 비어 있지 않은 DB (baseline-on-migrate 경로 검증)
            .withInitScript("llm-legacy-table.sql");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", mysql::getJdbcUrl);
        r.add("spring.datasource.username", mysql::getUsername);
        r.add("spring.datasource.password", mysql::getPassword);
        r.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        r.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQLDialect");
        r.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        r.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired PromptService promptService;
    @Autowired LlmCallLogService callLogService;
    @Autowired LlmCallLogRepository callLogRepository;

    @Test
    void tablesExistAfterMigrationOnNonEmptyDatabase() {
        List<String> tables = jdbc.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()", String.class);
        assertThat(tables).map(String::toLowerCase).contains("prompt_version", "llm_call_log");
    }

    @Test
    void promptKeyAndVersionAreUnique() {
        String insert = "INSERT INTO prompt_version (prompt_key, version, content, response_schema, model, change_note, created_at) "
                + "VALUES ('dup_key', 1, 'c', '{}', 'm', 'n', NOW())";
        jdbc.update(insert);

        assertThatThrownBy(() -> jdbc.update(insert)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void everyContractKeyIsSeededAsVersion1() {
        List<String> keys = jdbc.queryForList(
                "SELECT prompt_key FROM prompt_version WHERE version = 1", String.class);
        assertThat(keys).containsExactlyInAnyOrderElementsOf(EXPECTED_KEYS);

        Integer broken = jdbc.queryForObject(
                "SELECT COUNT(*) FROM prompt_version WHERE JSON_VALID(response_schema) = 0 "
                        + "OR content IS NULL OR CHAR_LENGTH(content) < 20 OR model IS NULL OR change_note IS NULL", Integer.class);
        assertThat(broken).isZero();
    }

    @Test
    void seededSchemasForcePredictableShapes() {
        PromptSpecDto mood = promptService.getActive("validate_mood");
        assertThat(mood.promptVersion()).isEqualTo(1);
        assertThat(mood.responseSchema().get("required").toString()).contains("is_valid", "normalized");
        assertThat(promptService.getActive("keyword_extract").content()).contains("{{ban_clause}}", "{{avoid_note}}");
        assertThat(promptService.getActive("chat_choose").content()).contains("{{user_name}}");
    }

    @Test
    void activeVersionIsTheHighestVersionAndOlderVersionsStayIntact() {
        jdbc.update("INSERT INTO prompt_version (prompt_key, version, content, response_schema, model, change_note, created_at) "
                + "VALUES ('chat_choose', 2, '새 본문 {{user_name}}', '{\"type\":\"object\"}', 'gpt-4o-mini', '말투 수정', NOW())");

        PromptSpecDto active = promptService.getActive("chat_choose");

        assertThat(active.promptVersion()).isEqualTo(2);
        assertThat(active.content()).isEqualTo("새 본문 {{user_name}}");
        String v1 = jdbc.queryForObject(
                "SELECT content FROM prompt_version WHERE prompt_key = 'chat_choose' AND version = 1", String.class);
        assertThat(v1).doesNotContain("새 본문");
    }

    @Test
    void callLogTableHasNoColumnForRawUserText() {
        List<String> cols = jdbc.queryForList(
                "SELECT column_name FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'llm_call_log'",
                String.class).stream().map(String::toLowerCase).toList();

        assertThat(cols).containsExactlyInAnyOrder(
                "id", "conversation_id", "flow_step", "slot", "request_id", "prompt_key", "prompt_version",
                "model", "latency_ms", "tokens_in", "tokens_out", "valid", "error_code", "created_at");
    }

    @Test
    void callLogServiceStoresOneRowPerCallSharingARequestId() {
        callLogService.record("s1", "hobby_check", List.of(
                new MlCallMeta("main", "chat_hobby_check_with_hobby", 1, "gpt-4o-mini", 812, 640, 41, true, null),
                new MlCallMeta("summary", "summary_update", 1, "gpt-4o-mini", 530, 410, 88, false, "SCHEMA_INVALID")));

        List<LlmCallLogEntity> rows = callLogRepository.findByConversationIdOrderByIdAsc("s1");

        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).getRequestId()).isNotBlank().isEqualTo(rows.get(1).getRequestId());
        assertThat(rows.get(0).getFlowStep()).isEqualTo("hobby_check");
        assertThat(rows.get(0).getPromptKey()).isEqualTo("chat_hobby_check_with_hobby");
        assertThat(rows.get(0).getPromptVersion()).isEqualTo(1);
        assertThat(rows.get(0).getLatencyMs()).isEqualTo(812);
        assertThat(rows.get(0).isValid()).isTrue();
        assertThat(rows.get(1).isValid()).isFalse();
        assertThat(rows.get(1).getErrorCode()).isEqualTo("SCHEMA_INVALID");
        assertThat(rows.get(1).getCreatedAt()).isNotNull();
    }
}
