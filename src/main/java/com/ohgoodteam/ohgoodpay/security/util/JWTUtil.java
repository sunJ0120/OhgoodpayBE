package com.ohgoodteam.ohgoodpay.security.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ohgoodteam.ohgoodpay.security.exception.AccessTokenException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;

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

    public String extractCustomerId(HttpServletRequest request) throws Exception {
        String token = extractTokenFromRequest(request);
        Map<String, Object> claims = validateToken(token);
        return (String) claims.get("customerId");
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) throws AccessTokenException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
    }
}
