package com.ohgoodteam.ohgoodpay.chat.controller;

import com.ohgoodteam.ohgoodpay.chat.dto.ChatRequest;
import com.ohgoodteam.ohgoodpay.chat.dto.ChatResponse;
import com.ohgoodteam.ohgoodpay.chat.service.ChatService;
import com.ohgoodteam.ohgoodpay.chat.util.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Chat")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public ApiResponseWrapper<ChatResponse> chat(@RequestBody ChatRequest chatRequest) {
        String userName = "오선정";
        ChatResponse response = chatService.chat(chatRequest.sessionId(), chatRequest.message(), userName);

        return ApiResponseWrapper.ok(response);
    }

    @PostMapping("/{sessionId}")
    public ApiResponseWrapper<Void> clearSession(@PathVariable String sessionId) {
        chatService.clearSession(sessionId);

        return ApiResponseWrapper.ok();
    }
}
