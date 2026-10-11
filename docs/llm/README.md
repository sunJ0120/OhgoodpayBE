# LLM 개선 ① — BE 정리 (프롬프트 버전 · 호출 기록 · 대화 흐름)

작성일 2026-10-11. 짝이 되는 ML 레포(OhgoodpayML)의 `docs/llm/`에 계약·결정 기록·후속 작업 전체가 있다. 이 문서는 **BE에서 알아야 할 것**만 정리한다.

## 1. 한눈에

`/api/chat`은 이제 Spring AI로 OpenAI를 직접 부르지 않는다. BE가 **대화 단계(flow)를 관리**하고, 각 단계의 **활성 프롬프트를 DB에서 조회**해 ML(`POST /ml/chat`, `/ml/chat/validation`)에 실어 보내며, 응답의 **호출 메타데이터를 `llm_call_log`에 저장**한다.

```
POST /api/chat {customerId, sessionId, message}
  start            → 고정 문장(기분 질문). ML 호출 없음
  mood_check 등    → ① 입력 검증(validate_*)  ② 유효: 값 저장 후 다음 단계 질문(chat_* / keyword_extract + summary_update)
                                               ③ 무효: 안내문(같은 단계에 최대 2회), 2번 실패하면 입력을 무시하고 다음 단계로
  LLM 실패         → 상태를 진행하지 않고 503 + data.errorCode
```

단계 전이: `start → mood_check → hobby_check → choose → recommendation → re-recommendation(반복)`

## 2. 용어

| 용어 | 정의 |
|---|---|
| 프롬프트 키 / 버전 / 활성 버전 | `prompt_version` 행의 `(prompt_key, version)`. 버전은 1부터 증가, **저장 후 수정하지 않음**. 활성 = 키별 `MAX(version)` |
| 슬롯 | 한 ML 요청 안의 호출 역할 `main`, `summary` |
| 호출 기록 | `llm_call_log` 1행 = LLM 호출 1건. **사용자 원문 컬럼 없음** |
| 오류 코드 | ML: `TIMEOUT`, `SCHEMA_INVALID`, `LLM_ERROR`, `TEMPLATE_ERROR` / BE: `ML_ERROR`(ML에 닿지 못함·4xx/5xx) |

## 3. 코드 지도

| 위치 | 역할 |
|---|---|
| `chat/service/ChatService` | 단계 상태기계. 검증 → 값 저장 → 다음 질문. 실패 시 `LlmServerException(errorCode)` |
| `chat/flow/ChatFlow` | 단계 enum, 전이, 단계별 프롬프트 키 매핑 |
| `chat/session/ChatSessionStore`, `RedisChatSessionStore` | 세션 상태(flow, 시도 횟수, 기분, 요약, 직전 키워드). Redis, TTL 1시간 |
| `llm/service/PromptService` | 활성 프롬프트 조회(캐시 없음) |
| `llm/service/LlmCallLogService` | ML 응답의 `calls[]` → `llm_call_log`. 저장 실패는 로그만 남기고 대화를 막지 않음 |
| `llm/client/MlChatClient(Impl)` | `fastapiWebClient`로 ML 호출. 본문은 로그에 찍지 않음 |
| `llm/entity`, `llm/repository` | `@Immutable` 엔티티. 프롬프트 저장소는 `Repository`를 상속하고 **조회 메서드만** 있음 |
| `db/migration/V202610111000__*` | `prompt_version`, `llm_call_log` 테이블 |
| `db/migration/V202610111010__*` | 프롬프트 12개 v1 시드(ML `docs/llm/seed/prompts_v1.json`에서 생성) |

## 4. 프롬프트를 바꾸는 절차

1. 새 버전을 `INSERT`하는 **새** 마이그레이션을 추가한다. 파일명 `V{YYYYMMDDHHMM}__설명.sql`.
   - **이미 적용된 마이그레이션 파일은 수정하지 않는다** (Flyway 체크섬 오류가 난다). `UPDATE`/`DELETE`로 옛 버전을 바꾸지 않는다.
   - 되돌릴 때는 옛 내용을 복사해 **더 큰 version**으로 추가한다.
   - 자리표시자 `{{user_name}}`, `{{hobby}}`, `{{mood}}`, `{{ban_clause}}`, `{{avoid_note}}`만 쓸 수 있다. 다른 이름은 ML이 `TEMPLATE_ERROR`로 거절한다.
   - SQL 문자열 안의 `'`는 `''`, `\`는 `\\`로 이스케이프한다. `${`는 Flyway 플레이스홀더와 충돌하니 쓰지 않는다.
   - `response_schema`는 OpenAI strict 조건을 지킨다: `additionalProperties: false`, 모든 속성을 `required`. 필드를 바꾸면 ML 코드의 소비부도 같이 바꿔야 한다.
2. ML 레포에서 평가 스크립트로 이전 버전과 비교한다(`eval/README.md`). 실모델 평가는 과금이 있어 수동이다.
3. 배포하면 Flyway가 적용하고, 캐시가 없어 즉시 반영된다.
4. 호출 기록으로 확인한다:

```sql
SELECT prompt_key, prompt_version, COUNT(*) AS n, SUM(valid) AS ok,
       ROUND(AVG(latency_ms)) AS avg_ms, SUM(error_code IS NOT NULL) AS errors
FROM llm_call_log WHERE created_at >= NOW() - INTERVAL 1 DAY
GROUP BY prompt_key, prompt_version ORDER BY prompt_key, prompt_version;
```

`llm_call_log.flow_step`은 검증 호출이면 **사용자가 답한 단계**, 채팅 호출이면 **생성한 다음 단계**다. `prompt_key`(`validate_*` vs `chat_*`)로 구분한다.

## 5. 실행·환경

| 항목 | 내용 |
|---|---|
| DB | MySQL 8.x. 환경변수 `DB_URL`(기본 `jdbc:mysql://localhost:3306/ohgoodpay…`), `DB_USERNAME`(기본 `root`), `DB_PASSWORD` |
| 로컬 MySQL | `docker compose up -d mysql` (포트 충돌 시 `MYSQL_PORT=3307`, 비밀번호 `DB_PASSWORD`, 기본 `ohgood`) |
| Flyway | 첫 실행은 `baseline-on-migrate`로 진행된다(이미 JPA 테이블이 있는 DB). DB 계정에 `CREATE`/`ALTER`/`INSERT`/`SELECT` 권한 필요(히스토리 테이블 `flyway_schema_history` 포함) |
| ML | `fastapi.yml`(`base-url`, `api-prefix: /ml`, `internal-token`, 타임아웃 connect 2s / read 25s)을 `spring.config.import`로 읽는다 |
| Redis | 세션 상태 키 `session:{flow,count,mood,summary,prev-keyword}:{sessionId}` |
| 테스트 | 레거시 테스트는 H2(`src/test/resources/application.properties`). DB 테스트는 **Docker 필요**(Testcontainers MySQL 8.4, Docker가 없으면 건너뜀). Docker Engine 29 호환을 위해 Testcontainers 1.21.4 |
| Gradle | `gradle/wrapper/gradle-wrapper.jar`가 레포에 없다(`.gitignore`의 `*.jar`). 로컬 Gradle 8.14로 실행하거나 IDE 사용 |

## 6. 프론트·클라이언트가 알아야 할 변화

- 요청의 **`customerId`가 필수**다(없으면 400). 이전 Spring AI 버전은 이름을 하드코딩하고 무시했다.
- 응답 형식(`sessionId`, `message`, `products[name,lprice,image,url,category]`)은 그대로다. 상품은 최대 3개.
- LLM 실패 시 `503` + `{"data": {"errorCode": "TIMEOUT"}}` 등. 상태가 진행되지 않으므로 같은 메시지를 다시 보내면 재시도된다.
- 추천 단계의 `message`는 빈 문자열이고 상품만 온다(기존 동작).

## 7. 한계와 후속

- **실제 OpenAI로는 검증하지 않았다**(사용자 결정: 목적은 구조 개선). 수동 확인은 OpenAI 호환 스텁으로 했다.
- 활성 프롬프트 캐시 없음, `llm_call_log` 보관 정리 없음, 기존 JPA 테이블의 Flyway 이관 없음.
- 네이버 실패 시 ML이 가짜 상품을 돌려주는 동작, 타임아웃·폴백 정책, 키워드 축소 재설계, 인기 상품 배치는 후속 작업이다(ML 레포 `docs/llm/overview.md` 7장).
- BE 전체 테스트 66개 중 7개 실패(`PayImmediately*` 6, `JwtTests` 1)는 이번 작업 이전부터의 문제다.
- 작업 시점에 커밋되지 않았다(브랜치 `refactor/chatbot`).
