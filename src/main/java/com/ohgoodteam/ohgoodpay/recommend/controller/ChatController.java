package com.ohgoodteam.ohgoodpay.recommend.controller;

import com.ohgoodteam.ohgoodpay.recommend.dto.ChatStartRequestDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.BasicChatResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.service.ChatService;
import com.ohgoodteam.ohgoodpay.recommend.util.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 추천 채팅 컨트롤러
 *
 * TODO : JwtUtil에서 가져오는 방식인데, 이거 @AuthenticationPrincipal(expression = "customerId") Long customerId 이거 쓸껀지 맞출건지는 고민 해봐야 할듯...
 */
@Tag(name = "Chat")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/chat")
    public ApiResponseWrapper<BasicChatResponseDTO> chat(
            @RequestBody ChatStartRequestDTO request
//            @AuthenticationPrincipal(expression = "customerId") Long customerId
            ) {
        try {
            BasicChatResponseDTO response = chatService.chat(
//                    customerId,
                    request.getCustomerId(),
                    request.getSessionId(),
                    request.getInputMessage()
            );
            return ApiResponseWrapper.ok(response);
        } catch (IllegalArgumentException e) {
            return ApiResponseWrapper.error(400, e.getMessage());
        } catch (RuntimeException e) {
            // 로깅만 추가해서 추적 가능하도록 설정
            log.warn("Chat service runtime error: {}", e.getMessage());
            return ApiResponseWrapper.error(500, "일시적인 서비스 오류가 발생했습니다");
        } catch (Exception e) {
            log.error("Unexpected error in chat controller", e);
            return ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다");
        }
    }
}
