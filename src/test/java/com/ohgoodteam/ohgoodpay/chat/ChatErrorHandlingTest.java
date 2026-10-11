package com.ohgoodteam.ohgoodpay.chat;

import com.ohgoodteam.ohgoodpay.chat.controller.ChatController;
import com.ohgoodteam.ohgoodpay.chat.dto.ChatResponse;
import com.ohgoodteam.ohgoodpay.chat.exception.GlobalExceptionHandler;
import com.ohgoodteam.ohgoodpay.chat.exception.LlmServerException;
import com.ohgoodteam.ohgoodpay.chat.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChatErrorHandlingTest {
    ChatService chatService = mock(ChatService.class);
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ChatController(chatService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void controllerPassesCustomerIdSessionAndMessage() throws Exception {
        when(chatService.chat(1L, "s1", "안녕")).thenReturn(new ChatResponse("s1", "기분 어때?", List.of()));

        mvc.perform(post("/api/chat").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":1,\"sessionId\":\"s1\",\"message\":\"안녕\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("기분 어때?"));
    }

    @Test
    void llmErrorCodeIsExposedToCaller() throws Exception {
        when(chatService.chat(any(), any(), any())).thenThrow(new LlmServerException("LLM 호출 실패", "TIMEOUT"));

        mvc.perform(post("/api/chat").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":1,\"sessionId\":\"s1\",\"message\":\"안녕\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value(503))
                .andExpect(jsonPath("$.data.errorCode").value("TIMEOUT"));
    }

    @Test
    void clearSessionDelegatesToService() throws Exception {
        mvc.perform(post("/api/chat/s1")).andExpect(status().isOk());
        verify(chatService).clearSession("s1");
    }
}
