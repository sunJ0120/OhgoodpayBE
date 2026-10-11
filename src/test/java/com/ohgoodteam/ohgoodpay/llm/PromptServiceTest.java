package com.ohgoodteam.ohgoodpay.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgoodteam.ohgoodpay.llm.dto.PromptSpecDto;
import com.ohgoodteam.ohgoodpay.llm.entity.PromptVersionEntity;
import com.ohgoodteam.ohgoodpay.llm.repository.PromptVersionRepository;
import com.ohgoodteam.ohgoodpay.llm.service.PromptService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PromptServiceTest {
    PromptVersionRepository repo = mock(PromptVersionRepository.class);
    PromptService service = new PromptService(repo, new ObjectMapper());

    @Test
    void returnsActiveVersionWithParsedSchema() {
        PromptVersionEntity e = mock(PromptVersionEntity.class);
        when(e.getPromptKey()).thenReturn("chat_choose");
        when(e.getVersion()).thenReturn(4);
        when(e.getContent()).thenReturn("안녕 {{user_name}}");
        when(e.getResponseSchema()).thenReturn("{\"type\":\"object\",\"required\":[\"message\"]}");
        when(e.getModel()).thenReturn("gpt-4o-mini");
        when(repo.findFirstByPromptKeyOrderByVersionDesc("chat_choose")).thenReturn(Optional.of(e));

        PromptSpecDto spec = service.getActive("chat_choose");

        assertThat(spec.promptKey()).isEqualTo("chat_choose");
        assertThat(spec.promptVersion()).isEqualTo(4);
        assertThat(spec.content()).isEqualTo("안녕 {{user_name}}");
        assertThat(spec.model()).isEqualTo("gpt-4o-mini");
        assertThat(spec.responseSchema().get("type").asText()).isEqualTo("object");
    }

    @Test
    void unknownKeyFailsLoudly() {
        when(repo.findFirstByPromptKeyOrderByVersionDesc("nope")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getActive("nope"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("nope");
    }
}
