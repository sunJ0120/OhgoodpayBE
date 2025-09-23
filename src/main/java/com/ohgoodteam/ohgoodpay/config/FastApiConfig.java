package com.ohgoodteam.ohgoodpay.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Validated
@PropertySource("classpath:fastapi.yml")
@ConfigurationProperties(prefix = "fastapi")
public class FastApiConfig {

    private String baseUrl;

    private String internalToken;

    private Http http = new Http();

    private Map<String, String> paths = new HashMap<>();

    private String apiPrefix = "";

    // public FastApiConfig() {
    //     this.baseUrl = "http://localhost:8000/ml";
    //     this.internalToken = "dev-token";
    // }

    @Getter @Setter
    public static class Http {
        private int connectTimeout = 2000;
        private int readTimeout = 5000;
    }
}
