package com.ohgoodteam.ohgoodpay.chat.util;

import com.ohgoodteam.ohgoodpay.chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
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

    public String chat(List<ChatMessage> history, String message) {
        List<Message> messages = buildPrompt(history, message);
        Prompt prompt = new Prompt(messages);

        ChatResponse response = chatModel.call(prompt);

        return response.getResult()
                .getOutput()
                .getText();
    }

    private List<Message> buildPrompt(List<ChatMessage> history, String message) {
        List<Message> messages = new ArrayList<>();

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
