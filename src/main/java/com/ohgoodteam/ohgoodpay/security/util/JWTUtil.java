package com.ohgoodteam.ohgoodpay.security.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtil {
    
    @Value("${jwt.secret.key}")
    private String secretKey;

    public String generateToken(Map<String, Object> claims, int days) {
        // header
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        // payload
        Map<String, Object> payloads = new HashMap<>();
        payloads.putAll(claims);

        // 유효기간 (밀리초 단위로 계산)
        long time = 60L * 60 * 24 * days * 1000; // 하루 = 60초 * 60분 * 24시간 * 1000밀리초

        return Jwts.builder()
            .setHeader(headers)
            .setClaims(payloads)
            .setExpiration(new Date(System.currentTimeMillis() + time))
            .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
            .compact();
    }

    public Map<String, Object> validateToken(String token) throws Exception {

        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
