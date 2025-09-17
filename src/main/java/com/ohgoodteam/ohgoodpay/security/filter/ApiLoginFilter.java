package com.ohgoodteam.ohgoodpay.security.filter;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ApiLoginFilter extends AbstractAuthenticationProcessingFilter {
    
    public ApiLoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Map<String, Object> map = new HashMap<>();
        try (Reader reader = new InputStreamReader(request.getInputStream())) {
            ObjectMapper om = new ObjectMapper();
            map = om.readValue(reader, Map.class);
        } catch (Exception e) {
            // e.printStackTrace();
        }

        // 인증처리
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(map.get("emailId"), map.get("pwd"));
        return getAuthenticationManager().authenticate(authenticationToken);
    }
    
}
