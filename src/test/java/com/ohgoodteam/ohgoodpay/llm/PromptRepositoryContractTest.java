package com.ohgoodteam.ohgoodpay.llm;

import com.ohgoodteam.ohgoodpay.llm.entity.PromptVersionEntity;
import com.ohgoodteam.ohgoodpay.llm.repository.PromptVersionRepository;
import org.hibernate.annotations.Immutable;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/** 프롬프트 버전은 수정·삭제하지 않는다. 코드에 그 수단 자체가 없어야 한다. */
class PromptRepositoryContractTest {
    private static final String[] WRITE_PREFIXES = {"save", "delete", "update", "remove", "persist", "merge", "flush"};

    @Test
    void repositoryDoesNotExtendCrudRepository() {
        assertThat(CrudRepository.class.isAssignableFrom(PromptVersionRepository.class)).isFalse();
        assertThat(PromptVersionRepository.class.getInterfaces()).containsExactly(Repository.class);
    }

    @Test
    void repositoryDeclaresOnlyReadMethods() {
        for (Method m : PromptVersionRepository.class.getMethods()) {
            for (String p : WRITE_PREFIXES) {
                assertThat(m.getName()).as(m.toString()).doesNotStartWith(p);
            }
        }
        assertThat(PromptVersionRepository.class.getMethods()).isNotEmpty();
    }

    @Test
    void entityIsImmutableAndHasNoSetters() {
        assertThat(PromptVersionEntity.class.isAnnotationPresent(Immutable.class)).isTrue();
        assertThat(Arrays.stream(PromptVersionEntity.class.getDeclaredMethods()).map(Method::getName))
                .noneMatch(n -> n.startsWith("set"));
    }
}
