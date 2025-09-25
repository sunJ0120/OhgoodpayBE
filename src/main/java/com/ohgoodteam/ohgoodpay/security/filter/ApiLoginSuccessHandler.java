package com.ohgoodteam.ohgoodpay.security.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgoodteam.ohgoodpay.security.dto.ApiCustomerDTO;
import com.ohgoodteam.ohgoodpay.security.util.JWTUtil;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

public class ApiLoginSuccessHandler implements AuthenticationSuccessHandler{

    private JWTUtil jwtUtil;

    public ApiLoginSuccessHandler(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 인증 성공 시 토큰 생성
     * @param request 요청
     * @param response 응답
     * @param authentication 인증 정보
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> claim = Map.of("customerId", authentication.getName());
        // Access Token 유효기간 1일
        String accessToken = jwtUtil.generateToken(claim, 1);

        Map<String, String> keyMap = Map.of("accessToken", accessToken);
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(keyMap);
        response.getWriter().print(json);
    }
    
}
