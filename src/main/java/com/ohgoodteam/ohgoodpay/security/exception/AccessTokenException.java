package com.ohgoodteam.ohgoodpay.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.Map;

public class AccessTokenException extends RuntimeException {
    TOKEN_ERROR token_error;

    /**
     * 토큰 오류 코드
     */
    public enum TOKEN_ERROR {
        UNACCEPT(401, "Token is null or too short"),
        BADTYPE(401, "Token type Bearer"),
        MALFORM(403, "Malformed Token"),
        BADSIGN(403, "BadSignatured Token"),
        EXPIRED(403, "Expired Token");

        private int status;
        private String msg;

        /**
         * 토큰 오류 코드 생성
         * @param status 상태 코드
         * @param msg 오류 메시지
         */
        TOKEN_ERROR(int status, String msg) {
            this.status = status;
            this.msg = msg;
        }
        /**
         * 상태 코드 반환
         * @return 상태 코드
         */
        public int getStatus() {
            return this.status;
        }
        /**
         * 오류 메시지 반환
         * @return 오류 메시지
         */
        public String getMsg() {
            return this.msg;
        }
    }
    /**
     * 토큰 오류 코드 생성
     * @param error 토큰 오류 코드
     */
    public AccessTokenException(TOKEN_ERROR error) {
        super(error.name());
        this.token_error = error;
    }
    /**
     * 오류 응답 전송
     * @param response 응답
     */
    public void sendResponseError(HttpServletResponse response) {
        response.setStatus(token_error.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try {
            ObjectMapper om = new ObjectMapper();

            String responseStr = om.writeValueAsString(Map.of("msg", token_error.getMsg(), "time", new Date()));
            response.getWriter().print(responseStr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
