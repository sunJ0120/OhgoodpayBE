package com.ohgoodteam.ohgoodpay.chat.util;

import com.ohgoodteam.ohgoodpay.chat.dto.ChatMessage;
import com.ohgoodteam.ohgoodpay.chat.exception.LlmServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class LlmApiClient {

    private final ChatModel chatModel;

    public String chat(List<ChatMessage> history, String message, String systemPrompt) {
        List<Message> messages = buildPrompt(history, message, systemPrompt);
        Prompt prompt = new Prompt(messages);

        try {
            ChatResponse response = chatModel.call(prompt);

            if (response == null || response.getResult() == null) {
                throw new LlmServerException("LLM 응답이 비어있습니다");
            }

            return response.getResult()
                    .getOutput()
                    .getText();
        } catch (Exception e) {
            log.error("LLM API 호출 실패: {}", e.getMessage(), e);
            throw new LlmServerException("LLM 서비스 호출 중 오류가 발생했습니다");
        }
    }

    private List<Message> buildPrompt(List<ChatMessage> history, String message, String systemPrompt) {
        List<Message> messages = new ArrayList<>();

        messages.add(new SystemMessage(systemPrompt));

        for (ChatMessage chatMessage : history) {
            if ("user".equals(chatMessage.role())) {
                messages.add(new UserMessage(
                        chatMessage.content()
                ));
            }

            if ("assistant".equals(chatMessage.role())) {
                messages.add(new AssistantMessage(
                        chatMessage.content()
                ));
            }
        }

        // 마지막 사용자 입력
        messages.add(new UserMessage(message));
        return messages;
    }
}
