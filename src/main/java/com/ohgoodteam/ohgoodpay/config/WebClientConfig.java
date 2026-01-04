package com.ohgoodteam.ohgoodpay.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(FastApiConfig.class)
/* 추후 sse 설정시
@Bean("fastapiSseWebClient") + ReadTimeoutHandler 제거
 */
public class WebClientConfig {

    private final FastApiConfig fastApiConfig;

    /** FastAPI(JSON) 전용 WebClient */
    @Bean(name = "fastapiWebClient")
    public WebClient fastapiWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, fastApiConfig.getHttp().getConnectTimeout())
                .doOnConnected(conn ->
                        conn.addHandlerLast(
                                new ReadTimeoutHandler(fastApiConfig.getHttp().getReadTimeout(), TimeUnit.MILLISECONDS)
                        )
                );

        return WebClient.builder()
                .baseUrl(fastApiConfig.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
