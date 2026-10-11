package com.ohgoodteam.ohgoodpay.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 실제 application.yml 이 fastapi.yml 을 불러와 FastApiConfig 에 바인딩하는지 확인한다.
 * (@PropertySource 는 YAML 을 읽지 못해서, 바인딩이 안 되면 base-url 이 비어 localhost:80 으로 호출하게 된다.)
 */
@SpringBootTest(classes = FastApiConfigBindingTest.Cfg.class)
class FastApiConfigBindingTest {

    @Configuration
    @EnableConfigurationProperties(FastApiConfig.class)
    static class Cfg {
    }

    @Autowired FastApiConfig cfg;

    @Test
    void baseUrlAndPrefixAreBoundFromFastApiYml() {
        assertThat(cfg.getBaseUrl()).isEqualTo("http://localhost:8000");
        assertThat(cfg.getApiPrefix()).isEqualTo("/ml");
        assertThat(cfg.getInternalToken()).isEqualTo("dev-token");
    }

    @Test
    void timeoutsAreUnchanged() {
        assertThat(cfg.getHttp().getConnectTimeout()).isEqualTo(2000);
        assertThat(cfg.getHttp().getReadTimeout()).isEqualTo(25000);
    }

    @Test
    void chatPathsAreConfigured() {
        assertThat(cfg.getPaths()).containsEntry("chat", "/chat").containsEntry("chatValidation", "/chat/validation");
    }
}
