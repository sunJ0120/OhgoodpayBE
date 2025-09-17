package com.ohgoodteam.ohgoodpay.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ohgoodteam.ohgoodpay.config.CorsConfig;
import com.ohgoodteam.ohgoodpay.security.filter.ApiLoginFilter;
import com.ohgoodteam.ohgoodpay.security.filter.ApiLoginSuccessHandler;
import com.ohgoodteam.ohgoodpay.security.filter.TokenCheckFilter;
import com.ohgoodteam.ohgoodpay.security.service.ApiCustomerDetailsService;
import com.ohgoodteam.ohgoodpay.security.util.JWTUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    
    private final JWTUtil jwtUtil;
    private final CorsConfig corsConfig;
    private final ApiCustomerDetailsService apiCustomerDetailsService;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**", "/api/public/**").permitAll()
            .anyRequest().authenticated());
        
        http.csrf(csrf -> csrf.disable());

        http.cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()));

        http.formLogin(login -> login.loginPage("/"));
        http.logout(logout -> logout.logoutSuccessUrl("/"));

        // JWT 관련 설정
		// AuthenticationManager 설정
        AuthenticationManagerBuilder authenticationManagerBuilder = http
        .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(apiCustomerDetailsService).passwordEncoder(passwordEncoder());

        // AuthenticationManager 객체 생성
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        http.authenticationManager(authenticationManager);

        // 필터
		ApiLoginFilter apiLoginFilter = new ApiLoginFilter("/auth"); // 토큰발급URL (http://localhost:8080/auth)
		apiLoginFilter.setAuthenticationManager(authenticationManager);
		apiLoginFilter.setAuthenticationSuccessHandler(new ApiLoginSuccessHandler(jwtUtil));

        // 필터동작위치
		http.addFilterBefore(apiLoginFilter, UsernamePasswordAuthenticationFilter.class);

        // 토큰체크필터
        TokenCheckFilter tokenCheckFilter = new TokenCheckFilter(jwtUtil);
		http.addFilterBefore(tokenCheckFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
