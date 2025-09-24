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

    /**
     * 토큰 생성
     * @param claims 토큰 페이로드
     * @param days 토큰 유효기간 (일)
     * @return
     */
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

    /**
     * 토큰 검증 (페이로드 반환)
     * @param token 토큰
     * @return
     * @throws Exception
     */
    public Map<String, Object> validateToken(String token) throws Exception {

        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰에서 사용자 ID 추출
     * @param request 요청
     * @return 사용자 ID
     * @throws Exception
     */
    public String extractCustomerId(HttpServletRequest request) throws Exception {
        String token = extractTokenFromRequest(request);
        Map<String, Object> claims = validateToken(token);
        return (String) claims.get("customerId");
    }
    
    /**
     * 요청에서 토큰 추출 후 Bearer 확인
     * @param request 요청
     * @return 토큰
     * @throws AccessTokenException
     */
    private String extractTokenFromRequest(HttpServletRequest request) throws AccessTokenException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
    }
}
