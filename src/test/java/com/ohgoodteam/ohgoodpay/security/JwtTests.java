package com.ohgoodteam.ohgoodpay.security;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ohgoodteam.ohgoodpay.security.util.JWTUtil;

@SpringBootTest
public class JwtTests {

    @Autowired
    private JWTUtil jwtUtil;

    @Test
    public void testGenerate() {
        // 올바른 토큰 페이로드 생성
        Map<String, Object> claimMap = Map.of("customerId", "1");
        System.out.println(claimMap);
        String jwtStr = jwtUtil.generateToken(claimMap, 1);
        System.out.println(jwtStr);
    }

    @Test
    public void testVaild() {
        String jwtStr = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjdXN0b21lcklkIjoiMSIsImV4cCI6MTc1ODE2NjQ5N30.PdPUdrBzi9Mxk58dLWIAB4eMY7mA4lkuK-QxGkp5nlM";
        Map<String, Object> claim;
        try {
            claim = jwtUtil.validateToken(jwtStr);
            System.out.println(claim);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
