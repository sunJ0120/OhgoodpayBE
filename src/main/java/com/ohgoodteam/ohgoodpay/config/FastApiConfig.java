package com.ohgoodteam.ohgoodpay.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "fastapi")
public class FastApiConfig {

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String internalToken;

    private Http http = new Http();

    private Map<String, String> paths = new HashMap<>();

    private String apiPrefix = "";

    @Getter @Setter
    public static class Http {
        private int connectTimeout = 2000;
        private int readTimeout = 5000;
    }
}
