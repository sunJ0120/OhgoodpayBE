package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.ChatStartResponse;

public interface ChatService {
    ChatStartResponse startChat(Long customerId);
}
