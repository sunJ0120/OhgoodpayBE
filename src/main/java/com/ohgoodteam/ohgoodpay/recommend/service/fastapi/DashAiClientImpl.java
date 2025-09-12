package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgoodteam.ohgoodpay.config.FastApiConfig;
import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeRequestDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.SayMyNameDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.SpendingAnalyzeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component("dashAiClientImpl")
@RequiredArgsConstructor
public class DashAiClientImpl implements DashAiClient {

    @Qualifier("fastapiWebClient")
    private final WebClient webClient;
    private final FastApiConfig cfg;
    private final ObjectMapper objectMapper;

    @Override
    public SayMyNameDTO.Out sayMyName(SayMyNameDTO.In in) {
        final String path = resolvedPath("sayMyName", "/dash/saymyname");
        final int readTimeoutMs = Optional.ofNullable(cfg.getHttp())
                .map(FastApiConfig.Http::getReadTimeout).orElse(5000);

        // 직전 JSON 스냅샷
        try {
            String json = objectMapper.writeValueAsString(in);
            log.info("[AI->] POST {} payload.json={}", path, json);
            System.out.println(">>> DEBUG OUT " + json);
        } catch (Exception e) {
            log.warn("[AI->] JSON serialize failed: {}", e.toString());
        }
        return webClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Internal-Token", cfg.getInternalToken())
                .bodyValue(in)
                .retrieve()
                // 에러 응답일 때 상태/본문 같이 로깅
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .doOnNext(body -> log.warn("[AI<-] {} body={}", resp.statusCode(), body))
                                .flatMap(body -> resp.createException().flatMap(Mono::error))
                )
                // 성공 응답 DTO 로깅
                .bodyToMono(SayMyNameDTO.Out.class)
                .doOnNext(out -> log.info("[AI<-] 200 out={}", out))
                .timeout(Duration.ofMillis(readTimeoutMs))
                .block();
    }

    // DashAiClientImpl.java (핵심 메서드만)
    @Override
    public SpendingAnalyzeDTO.Out analyzeSpending(SpendingAnalyzeDTO.In in) {
        final String path = resolvedPath("analyzeSpending", "/dash/analyze");
        final int readTimeoutMs = Optional.ofNullable(cfg.getHttp())
                .map(FastApiConfig.Http::getReadTimeout).orElse(5000);

        try { log.info("[AI->] POST {} tx.size={}", path, in.getTransactions()==null?0:in.getTransactions().size()); }
        catch (Exception ignore) {}

        return webClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Internal-Token", cfg.getInternalToken())
                .bodyValue(in)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .doOnNext(body -> log.warn("[AI<-] {} body={}", resp.statusCode(), body))
                                .flatMap(body -> resp.createException().flatMap(Mono::error))
                )
                .bodyToMono(SpendingAnalyzeDTO.Out.class)
                .timeout(Duration.ofMillis(readTimeoutMs))
                .block();
    }


    // ---------- util ----------
    private String resolvedPath(String key, String defaultPath) {
        String prefix = Optional.ofNullable(cfg.getApiPrefix()).orElse("");
        String endpoint = Optional.ofNullable(cfg.getPaths()).map(m -> m.get(key)).orElse(defaultPath);
        return join(prefix, endpoint);
    }
    private static String join(String a, String b) {
        if (a == null || a.isBlank()) return b.startsWith("/") ? b : "/" + b;
        String left  = a.endsWith("/") ? a.substring(0, a.length()-1) : a;
        String right = b.startsWith("/") ? b : "/" + b;
        return left + right;
    }
}