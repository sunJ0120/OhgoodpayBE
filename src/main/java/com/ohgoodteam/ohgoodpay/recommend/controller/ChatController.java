package com.ohgoodteam.ohgoodpay.recommend.controller;

import com.ohgoodteam.ohgoodpay.recommend.dto.ChatStartRequestDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.BasicChatResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.service.ChatService;
import com.ohgoodteam.ohgoodpay.recommend.util.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 추천 채팅 컨트롤러
 */
@Tag(name = "Chat")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    // TODO : 에러 정의를 좀 더 세분화 하는 방향 고려
    @PostMapping()
    public ApiResponseWrapper<BasicChatResponseDTO> chat(
            @RequestBody ChatStartRequestDTO request,
            HttpServletRequest httpRequest) {
        try {
            // JWT에서 customerId 추출하는 파트 미리 만들어 놓음
//            String customerId = jwtUtil.getCustomerIdFromToken(httpRequest);

            BasicChatResponseDTO response = chatService.chat(
//                    customerId,
                    request.getCustomerId(),
                    request.getSessionId(),
                    request.getInputMessage(),
                    request.getFlow()
            );
            return ApiResponseWrapper.ok(response);

        } catch (IllegalArgumentException e) {
            return ApiResponseWrapper.error(400, e.getMessage());

        } catch (Exception e) {
            return ApiResponseWrapper.error(500, "서버 내부 오류가 발생했습니다");
        }
    }
}
