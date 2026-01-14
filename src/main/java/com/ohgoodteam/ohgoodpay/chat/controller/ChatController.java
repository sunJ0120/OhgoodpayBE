package com.ohgoodteam.ohgoodpay.chat.controller;

import com.ohgoodteam.ohgoodpay.common.dto.ClearSessionRequestDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.ChatStartRequestDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.BasicChatResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.service.ChatCacheService;
import com.ohgoodteam.ohgoodpay.recommend.service.ChatService;
import com.ohgoodteam.ohgoodpay.recommend.util.ApiResponseWrapper;
import com.ohgoodteam.ohgoodpay.security.util.JWTUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 추천 채팅 컨트롤러
 *
 */
@Tag(name = "Chat")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;
    private final ChatCacheService chatCacheService;
    private final JWTUtil jwtUtil; //jwtUtil 사용하는 방식으로 변경

    @PostMapping("")
    public ApiResponseWrapper<BasicChatResponseDTO> chat( @RequestBody ChatStartRequestDTO chatRequest, HttpServletRequest request) {
        try {
            Long customerId = Long.parseLong(jwtUtil.extractCustomerId(request));

            BasicChatResponseDTO response = chatService.chat(
                    customerId,
                    chatRequest.getSessionId(),
                    chatRequest.getInputMessage(),
                    chatRequest.getFlow()
            );

            return ApiResponseWrapper.ok(response);
        } catch (IllegalArgumentException e) {
            return ApiResponseWrapper.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return ApiResponseWrapper.error(500, "일시적인 서비스 오류가 발생했습니다");
        } catch (Exception e) {
            return ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다");
        }
    }

    // 초기화나 화면을 떠날 시, 세션 기반 정보들을 전부 삭제하기 위함
    @PostMapping("/clear-session")
    public ApiResponseWrapper<Void> clearSession(@RequestBody ClearSessionRequestDTO clearSessionRequest, HttpServletRequest request) {
        try {
            Long customerId = Long.parseLong(jwtUtil.extractCustomerId(request));
            chatCacheService.clearSessionCache(clearSessionRequest.getSessionId());

            return ApiResponseWrapper.ok(null);
        } catch (IllegalArgumentException e) {
            return ApiResponseWrapper.error(400, e.getMessage());
        } catch (RuntimeException e) {
            return ApiResponseWrapper.error(500, "일시적인 서비스 오류가 발생했습니다");
        } catch (Exception e) {
            return ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다");
        }
    }

}
