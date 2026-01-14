package com.ohgoodteam.ohgoodpay.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger 전역 설정
 * OhGoodPay 전체 서비스 API 문서화
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI swaggerAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OhGoodPay Backend API")
                        .description("OhGoodPay 금융 플랫폼 전체 서비스 API 문서")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("OhGoodTeam")
                                .email("dev@ohgoodpay.com")
                                .url("https://github.com/OhGoodTeam/OhgoodpayBE")))
                                .servers(List.of(
                                      new Server().url("http://localhost:8080").description("개발 로컬 서버")
                ))
                .tags(List.of(
                        new Tag() // Controller 별로 태그 맞춰서 지정해주면 됩니다. 임시로 도메인 별로 만들어뒀습니다.
                                .name("Auth")
                                .description("사용자 인증 및 권한 관리"),
                        new Tag()
                                .name("Pay")
                                .description("결제 및 송금 서비스"),
                        new Tag()
                                .name("Chat")
                                .description("AI 추천 챗봇 관련 API"),
                        new Tag()
                                .name("Dash")
                                .description("AI 사용자 대시보드 관련 API"),
                        new Tag()
                                .name("Shorts")
                                .description("쇼츠 기능 API")
                ));
    }
}

