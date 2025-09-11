package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgoodteam.ohgoodpay.config.FastApiConfig;
//import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeRequest;
//import com.ohgoodteam.ohgoodpay.recommend.dto.SpendingAnalyzeResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.SayMyNameDTO;
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
        final String path = resolvedPath();
        final int readTimeoutMs = Optional.ofNullable(cfg.getHttp())
                .map(FastApiConfig.Http::getReadTimeout).orElse(5000);

        // ① 보내기 직전 JSON 스냅샷
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
                // ② 에러 응답일 때 상태/본문 같이 로깅
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .doOnNext(body -> log.warn("[AI<-] {} body={}", resp.statusCode(), body))
                                .flatMap(body -> resp.createException().flatMap(Mono::error))
                )
                // ③ 성공 응답 DTO 로깅 (필요시 원문 JSON도 아래 대안 참고)
                .bodyToMono(SayMyNameDTO.Out.class)
                .doOnNext(out -> log.info("[AI<-] 200 out={}", out))
                .timeout(Duration.ofMillis(readTimeoutMs))
                .block();
    }

//    @Override
//    public SpendingAnalyzeResponse analyzeSpending(SpendingAnalyzeRequest req) {
//        String endpoint = Optional.ofNullable(cfg.getPaths()).map(m -> m.get("analyzeSpending"))
//                .orElse("/dash/analyze");
//        String path = Optional.ofNullable(cfg.getApiPrefix()).orElse("") +
//                (endpoint.startsWith("/") ? endpoint : "/" + endpoint);
//
//        int readTimeoutMs = Optional.ofNullable(cfg.getHttp())
//                .map(FastApiConfig.Http::getReadTimeout).orElse(5000);
//
//        log.info("[AI->] POST {} transactions={}", path,
//                req.getTransactions() == null ? 0 : req.getTransactions().size());
//
//        return webClient.post()
//                .uri(path)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .bodyValue(req)
//                .retrieve()
//                .onStatus(HttpStatusCode::isError, resp -> resp.createException().flatMap(Mono::error))
//                .bodyToMono(SpendingAnalyzeResponse.class)
//                .timeout(Duration.ofMillis(readTimeoutMs))
//                .block();
//    }

    // ---------- 유틸 ----------
    private String resolvedPath() {
        String prefix = Optional.ofNullable(cfg.getApiPrefix()).orElse("");
        String endpoint = Optional.ofNullable(cfg.getPaths())
                .map(m -> m.get("sayMyName"))
                .orElse("/dash/saymyname");
        return join(prefix, endpoint); // -> /ml/dash/saymyname
    }
    private static String join(String a, String b) {
        if (a == null || a.isBlank()) return b.startsWith("/") ? b : "/" + b;
        String left  = a.endsWith("/") ? a.substring(0, a.length()-1) : a;
        String right = b.startsWith("/") ? b : "/" + b;
        return left + right;
    }
}
