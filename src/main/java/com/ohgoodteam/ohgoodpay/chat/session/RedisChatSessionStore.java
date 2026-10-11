package com.ohgoodteam.ohgoodpay.chat.session;

import com.ohgoodteam.ohgoodpay.chat.flow.ChatFlow;
import com.ohgoodteam.ohgoodpay.chat.util.CacheSpec;
import com.ohgoodteam.ohgoodpay.chat.util.CacheStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisChatSessionStore implements ChatSessionStore {
    private final CacheStore cache;

    @Override
    public ChatFlow getFlow(String sessionId) {
        return ChatFlow.fromValue(cache.get(CacheSpec.FLOW.key(sessionId), String.class));
    }

    @Override
    public void saveFlow(String sessionId, ChatFlow flow) {
        cache.save(CacheSpec.FLOW.key(sessionId), flow.getValue(), CacheSpec.FLOW.getTtl());
    }

    @Override
    public int getCount(String sessionId) {
        Integer count = cache.get(CacheSpec.COUNT.key(sessionId), Integer.class);
        return count != null ? count : 1;
    }

    @Override
    public void saveCount(String sessionId, int count) {
        cache.save(CacheSpec.COUNT.key(sessionId), count, CacheSpec.COUNT.getTtl());
    }

    @Override
    public String getMood(String sessionId) {
        String mood = cache.get(CacheSpec.MOOD.key(sessionId), String.class);
        return mood != null ? mood : "";
    }

    @Override
    public void saveMood(String sessionId, String mood) {
        cache.save(CacheSpec.MOOD.key(sessionId), mood, CacheSpec.MOOD.getTtl());
    }

    @Override
    public String getSummary(String sessionId) {
        String summary = cache.get(CacheSpec.SUMMARY.key(sessionId), String.class);
        return summary != null ? summary : "";
    }

    @Override
    public void saveSummary(String sessionId, String summary) {
        cache.save(CacheSpec.SUMMARY.key(sessionId), summary, CacheSpec.SUMMARY.getTtl());
    }

    @Override
    public String getPrevKeyword(String sessionId) {
        return cache.get(CacheSpec.PREV_KEYWORD.key(sessionId), String.class);
    }

    @Override
    public void savePrevKeyword(String sessionId, String keyword) {
        cache.save(CacheSpec.PREV_KEYWORD.key(sessionId), keyword, CacheSpec.PREV_KEYWORD.getTtl());
    }

    @Override
    public void clear(String sessionId) {
        for (CacheSpec spec : CacheSpec.values()) {
            cache.delete(spec.key(sessionId));
        }
    }
}
