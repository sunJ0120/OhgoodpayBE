package com.ohgoodteam.ohgoodpay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정
 * 개발 단계에서 API 및 Swagger 접근 허용
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll() // API 경로 허용
                .requestMatchers("/swagger-ui/**").permitAll() // Swagger UI 허용
                .requestMatchers("/swagger-ui.html").permitAll() // Swagger UI 허용
                .requestMatchers("/v3/api-docs/**").permitAll() // OpenAPI 문서 허용
                .requestMatchers("/swagger-resources/**").permitAll() // Swagger 리소스 허용
                .requestMatchers("/webjars/**").permitAll() // Swagger 정적 리소스 허용
                .anyRequest().permitAll() // 개발 단계에서 모든 요청 허용
            );

        return http.build();
    }
}