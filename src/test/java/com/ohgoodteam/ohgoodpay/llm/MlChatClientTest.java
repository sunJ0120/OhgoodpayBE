package com.ohgoodteam.ohgoodpay.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgoodteam.ohgoodpay.chat.exception.LlmServerException;
import com.ohgoodteam.ohgoodpay.config.FastApiConfig;
import com.ohgoodteam.ohgoodpay.llm.client.MlChatClientImpl;
import com.ohgoodteam.ohgoodpay.llm.dto.*;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** JDK 내장 HttpServer 로 ML(FastAPI)을 흉내 내어 요청/응답 계약을 검증한다. */
class MlChatClientTest {
    static final ObjectMapper OM = new ObjectMapper();

    HttpServer server;
    AtomicReference<String> path = new AtomicReference<>();
    AtomicReference<String> token = new AtomicReference<>();
    AtomicReference<String> body = new AtomicReference<>();
    int status = 200;
    String responseJson = "{}";
    MlChatClientImpl client;

    @BeforeEach
    void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", ex -> {
            path.set(ex.getRequestURI().getPath());
            token.set(ex.getRequestHeaders().getFirst("X-Internal-Token"));
            body.set(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] out = responseJson.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().add("Content-Type", "application/json");
            ex.sendResponseHeaders(status, out.length);
            ex.getResponseBody().write(out);
            ex.close();
        });
        server.start();
        client = newClient(server.getAddress().getPort());
    }

    @AfterEach
    void stop() { server.stop(0); }

    MlChatClientImpl newClient(int port) {
        FastApiConfig cfg = new FastApiConfig();
        cfg.setApiPrefix("/ml");
        cfg.setInternalToken("tok");
        return new MlChatClientImpl(WebClient.builder().baseUrl("http://127.0.0.1:" + port).build(), cfg);
    }

    static PromptSpecDto spec(String key, int v) {
        return new PromptSpecDto(key, v, "본문 {{user_name}}", OM.createObjectNode().put("type", "object"), "gpt-4o-mini");
    }

    MlChatRequest chatRequest() {
        return new MlChatRequest("s1", "choose", "러닝", new MlCustomerInfo(1L, "오선정", 300_000),
                "positive", "게임", 120_000, "요약", null,
                new MlPrompts(spec("chat_choose", 3), spec("summary_update", 2)));
    }

    @Test
    void chatRequestIsPostedToMlChatWithTokenAndCamelCaseBody() throws Exception {
        responseJson = "{\"sessionId\":\"s1\",\"message\":\"m\",\"newHobby\":\"\",\"products\":[],\"summary\":\"s\",\"flow\":\"choose\",\"calls\":[]}";

        client.chat(chatRequest());

        assertThat(path.get()).isEqualTo("/ml/chat");
        assertThat(token.get()).isEqualTo("tok");
        JsonNode j = OM.readTree(body.get());
        assertThat(j.get("sessionId").asText()).isEqualTo("s1");
        assertThat(j.get("inputMessage").asText()).isEqualTo("러닝");
        assertThat(j.get("customerInfo").get("creditLimit").asInt()).isEqualTo(300_000);
        assertThat(j.has("prevKeyword")).isTrue();
        assertThat(j.get("prompts").get("main").get("promptKey").asText()).isEqualTo("chat_choose");
        assertThat(j.get("prompts").get("main").get("promptVersion").asInt()).isEqualTo(3);
        assertThat(j.get("prompts").get("main").get("responseSchema").get("type").asText()).isEqualTo("object");
        assertThat(j.get("prompts").get("summary").get("promptKey").asText()).isEqualTo("summary_update");
    }

    @Test
    void chatResponseIsDeserializedIncludingCallsAndProducts() {
        responseJson = """
                {"sessionId":"s1","message":null,"newHobby":"","summary":"s","flow":"recommendation","keyword":"러닝화",
                 "result":{"main":{"keyword":"러닝화"}},
                 "products":[{"rank":1,"name":"러닝화A","price":39000,"image":"i","url":"u","category":"c"}],
                 "calls":[{"slot":"main","promptKey":"keyword_extract","promptVersion":4,"model":"gpt-4o-mini",
                           "latencyMs":812,"tokensIn":640,"tokensOut":41,"valid":false,"errorCode":"SCHEMA_INVALID"}],
                 "unknownFutureField":123}
                """;

        MlChatResponse res = client.chat(chatRequest());

        assertThat(res.message()).isNull();
        assertThat(res.keyword()).isEqualTo("러닝화");
        assertThat(res.products()).hasSize(1);
        assertThat(res.products().get(0).price()).isEqualTo(39_000);
        MlCallMeta c = res.calls().get(0);
        assertThat(c.slot()).isEqualTo("main");
        assertThat(c.promptKey()).isEqualTo("keyword_extract");
        assertThat(c.promptVersion()).isEqualTo(4);
        assertThat(c.latencyMs()).isEqualTo(812);
        assertThat(c.tokensIn()).isEqualTo(640);
        assertThat(c.valid()).isFalse();
        assertThat(c.errorCode()).isEqualTo("SCHEMA_INVALID");
    }

    @Test
    void validationIsPostedToMlChatValidation() throws Exception {
        responseJson = "{\"customerId\":1,\"sessionId\":\"s1\",\"inputMessage\":\"기분 좋아\",\"isValid\":true,\"message\":\"\",\"normalized\":\"positive\",\"flow\":\"mood_check\","
                + "\"calls\":[{\"slot\":\"main\",\"promptKey\":\"validate_mood\",\"promptVersion\":1,\"model\":\"m\",\"latencyMs\":1,\"tokensIn\":1,\"tokensOut\":1,\"valid\":true,\"errorCode\":null}]}";

        MlValidationResponse res = client.validate(new MlValidationRequest(
                1L, "s1", "기분 좋아", "mood_check", new MlPrompts(spec("validate_mood", 1), null)));

        assertThat(path.get()).isEqualTo("/ml/chat/validation");
        JsonNode j = OM.readTree(body.get());
        assertThat(j.get("customerId").asInt()).isEqualTo(1);
        assertThat(j.get("prompts").get("main").get("promptKey").asText()).isEqualTo("validate_mood");
        assertThat(res.isValid()).isTrue();
        assertThat(res.normalized()).isEqualTo("positive");
        assertThat(res.calls()).hasSize(1);
    }

    @Test
    void mlHttpErrorBecomesLlmServerExceptionWithMlErrorCode() {
        status = 422;
        responseJson = "{\"detail\":\"bad\"}";

        assertThatThrownBy(() -> client.chat(chatRequest()))
                .isInstanceOf(LlmServerException.class)
                .hasFieldOrPropertyWithValue("errorCode", "ML_ERROR");
    }

    @Test
    void mlConnectionFailureBecomesLlmServerExceptionWithMlErrorCode() {
        MlChatClientImpl dead = newClient(1);   // 아무도 듣지 않는 포트

        assertThatThrownBy(() -> dead.chat(chatRequest()))
                .isInstanceOf(LlmServerException.class)
                .hasFieldOrPropertyWithValue("errorCode", "ML_ERROR");
    }

    @Test
    void requestRecordsContainNoRawTextOnlyWhatMlNeeds() {
        // 계약상 ML 요청에만 사용자 입력이 포함된다 (로그 저장소로는 가지 않는다) — 필드 이름 고정
        assertThat(List.of(MlChatRequest.class.getRecordComponents()).stream().map(c -> c.getName()).toList())
                .containsExactly("sessionId", "flow", "inputMessage", "customerInfo", "mood", "hobby",
                        "balance", "summary", "prevKeyword", "prompts");
    }
}
