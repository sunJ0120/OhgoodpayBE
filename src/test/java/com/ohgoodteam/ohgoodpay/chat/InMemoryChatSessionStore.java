package com.ohgoodteam.ohgoodpay.chat;

import com.ohgoodteam.ohgoodpay.chat.flow.ChatFlow;
import com.ohgoodteam.ohgoodpay.chat.session.ChatSessionStore;

import java.util.HashMap;
import java.util.Map;

/** 테스트용: Redis 없이 세션 상태를 메모리에 둔다. */
public class InMemoryChatSessionStore implements ChatSessionStore {
    private final Map<String, ChatFlow> flow = new HashMap<>();
    private final Map<String, Integer> count = new HashMap<>();
    private final Map<String, String> mood = new HashMap<>();
    private final Map<String, String> summary = new HashMap<>();
    private final Map<String, String> prevKeyword = new HashMap<>();

    @Override public ChatFlow getFlow(String s) { return flow.getOrDefault(s, ChatFlow.START); }
    @Override public void saveFlow(String s, ChatFlow f) { flow.put(s, f); }
    @Override public int getCount(String s) { return count.getOrDefault(s, 1); }
    @Override public void saveCount(String s, int c) { count.put(s, c); }
    @Override public String getMood(String s) { return mood.getOrDefault(s, ""); }
    @Override public void saveMood(String s, String m) { mood.put(s, m); }
    @Override public String getSummary(String s) { return summary.getOrDefault(s, ""); }
    @Override public void saveSummary(String s, String v) { summary.put(s, v); }
    @Override public String getPrevKeyword(String s) { return prevKeyword.get(s); }
    @Override public void savePrevKeyword(String s, String k) { prevKeyword.put(s, k); }

    @Override
    public void clear(String s) {
        flow.remove(s); count.remove(s); mood.remove(s); summary.remove(s); prevKeyword.remove(s);
    }

    public boolean isEmpty(String s) {
        return !flow.containsKey(s) && !count.containsKey(s) && !mood.containsKey(s)
                && !summary.containsKey(s) && !prevKeyword.containsKey(s);
    }
}
