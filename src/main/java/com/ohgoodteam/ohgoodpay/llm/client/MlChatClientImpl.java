package com.ohgoodteam.ohgoodpay.llm.client;

import com.ohgoodteam.ohgoodpay.chat.exception.LlmServerException;
import com.ohgoodteam.ohgoodpay.config.FastApiConfig;
import com.ohgoodteam.ohgoodpay.llm.dto.MlChatRequest;
import com.ohgoodteam.ohgoodpay.llm.dto.MlChatResponse;
import com.ohgoodteam.ohgoodpay.llm.dto.MlValidationRequest;
import com.ohgoodteam.ohgoodpay.llm.dto.MlValidationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

/**
 * 타임아웃은 기존 fastapiWebClient 설정(connect 2s / read 25s)을 그대로 쓴다 (이번 작업에서 값은 바꾸지 않는다).
 * 요청·응답 본문에는 사용자 입력이 있으므로 로그에 찍지 않는다.
 */
@Slf4j
@Component
public class MlChatClientImpl implements MlChatClient {
    static final String ML_ERROR = "ML_ERROR";

    private final WebClient webClient;
    private final FastApiConfig cfg;

    public MlChatClientImpl(@Qualifier("fastapiWebClient") WebClient webClient, FastApiConfig cfg) {
        this.webClient = webClient;
        this.cfg = cfg;
    }

    @Override
    public MlChatResponse chat(MlChatRequest request) {
        return post(resolvedPath("chat", "/chat"), request, MlChatResponse.class);
    }

    @Override
    public MlValidationResponse validate(MlValidationRequest request) {
        return post(resolvedPath("chatValidation", "/chat/validation"), request, MlValidationResponse.class);
    }

    private <T> T post(String path, Object body, Class<T> type) {
        try {
            T res = webClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-Internal-Token", cfg.getInternalToken())
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(type)
                    .block();
            if (res == null) {
                throw new LlmServerException("ML 서버 응답이 비어 있습니다", ML_ERROR);
            }
            return res;
        } catch (LlmServerException e) {
            throw e;
        } catch (Exception e) {
            log.error("ML 서버 호출 실패: path={}, error={}", path, e.toString());
            throw new LlmServerException("ML 서버 호출 중 오류가 발생했습니다", ML_ERROR, e);
        }
    }

    private String resolvedPath(String key, String defaultPath) {
        String prefix = Optional.ofNullable(cfg.getApiPrefix()).orElse("");
        String endpoint = Optional.ofNullable(cfg.getPaths()).map(m -> m.get(key)).orElse(defaultPath);
        String left = prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix;
        String right = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
        return left + right;
    }
}
