package com.ohgoodteam.ohgoodpay.recommend.service.fastapi;

import com.ohgoodteam.ohgoodpay.config.FastApiConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class AdviceAiClientImpl implements AdviceAiClient {

    @Qualifier("fastapiWebClient")
    private final WebClient webClient;
    private final FastApiConfig cfg;

    // 메서드 생기면 DashAiClientImpl 패턴과 동일하게 구현
}
